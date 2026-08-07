package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MemberWriter#L35 관련 실제 DB 기반 동시성 테스트
 * MemberCreator를 REQUIRES_NEW로 격리한 게 실제로 동작하는지 진짜 트랜잭션으로 검증
 * <p>
 * 실행하려면 DB_URL 등 환경변수로 실제 MySQL을 가리켜야 함
 */
@SpringBootTest
class MemberWriterIntegrationTest {

    @Autowired
    private MemberWriter memberWriter;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberSettingRepository memberSettingRepository;

    @Autowired
    private MemberCreator memberCreator;

    private String createdProviderId;

    @AfterEach
    void cleanUp() {
        if (createdProviderId != null) {
            memberRepository.findByProviderAndProviderId(Provider.KAKAO, createdProviderId)
                    .ifPresent(member -> {
                        memberSettingRepository.findByMemberId(member.getId())
                                .ifPresent(memberSettingRepository::delete);
                        memberRepository.delete(member);
                    });
        }
    }

    @Test
    void 동시_로그인해도_회원은_한_명만_생성되고_둘_다_정상_응답을_받는다() throws Exception {
        Provider provider = Provider.KAKAO;
        OidcUserInfo oidcUserInfo = new OidcUserInfo(
                "concurrent-test-" + UUID.randomUUID(), "concurrent@test.com");
        createdProviderId = oidcUserInfo.providerId();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<MemberResDTO.Login> task = () -> {
            ready.countDown();
            start.await();
            return memberWriter.persist(provider, oidcUserInfo);
        };

        try {
            Future<MemberResDTO.Login> f1 = executor.submit(task);
            Future<MemberResDTO.Login> f2 = executor.submit(task);

            ready.await(5, TimeUnit.SECONDS);
            start.countDown();

            // 실패했다면 여기서 ExecutionException(UnexpectedRollbackException 등)이 그대로 터짐
            MemberResDTO.Login result1 = f1.get(15, TimeUnit.SECONDS);
            MemberResDTO.Login result2 = f2.get(15, TimeUnit.SECONDS);

            assertThat(result1.accessToken()).isNotBlank();
            assertThat(result2.accessToken()).isNotBlank();
        } finally {
            executor.shutdown();
        }

        List<Member> created = memberRepository.findAll().stream()
                .filter(m -> oidcUserInfo.providerId().equals(m.getProviderId()))
                .toList();
        assertThat(created).hasSize(1);
    }

    /*
     * 둘 다 없음을 본 상태를 먼저 고정한 뒤, persist()의 조회 단계를 건너뛰고
     * MemberCreator.create()로 바로 밀어넣어 경합을 결정적으로 재현
     */
    @Test
    void 두_요청의_최초_조회가_모두_비어있음을_확인한_뒤_동시에_생성하면_한쪽만_성공하고_MemberWriter가_복구한다() throws Exception {
        Provider provider = Provider.KAKAO;
        OidcUserInfo oidcUserInfo = new OidcUserInfo(
                "concurrent-race-" + UUID.randomUUID(), "concurrent-race@test.com");
        createdProviderId = oidcUserInfo.providerId();

        // 경합 전제조건 고정: 두 요청 모두 최초 조회 시점엔 회원이 없음
        assertThat(memberRepository.findByProviderAndProviderId(provider, oidcUserInfo.providerId())).isEmpty();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Member> task = () -> {
            ready.countDown();
            start.await();
            return memberCreator.create(provider, oidcUserInfo);
        };

        int successCount = 0;
        int conflictCount = 0;
        try {
            Future<Member> f1 = executor.submit(task);
            Future<Member> f2 = executor.submit(task);

            ready.await(5, TimeUnit.SECONDS);
            start.countDown();

            for (Future<Member> f : List.of(f1, f2)) {
                try {
                    f.get(15, TimeUnit.SECONDS);
                    successCount++;
                } catch (ExecutionException e) {
                    assertThat(e.getCause()).isInstanceOf(DataIntegrityViolationException.class);
                    conflictCount++;
                }
            }
        } finally {
            executor.shutdown();
        }

        // 한쪽만 생성에 성공하고 한쪽은 유니크 제약 위반으로 실패해야 함
        assertThat(successCount).isEqualTo(1);
        assertThat(conflictCount).isEqualTo(1);

        List<Member> created = memberRepository.findAll().stream()
                .filter(m -> oidcUserInfo.providerId().equals(m.getProviderId()))
                .toList();
        assertThat(created).hasSize(1);

        // MemberWriter.persist()를 다시 호출 -> catch(DataIntegrityViolationException)
        // 정상적으로 기존 회원을 찾아 로그인 처리하는지, refreshTokenHash가 실제로 DB에 반영되는지 확인
        MemberResDTO.Login response = memberWriter.persist(provider, oidcUserInfo);
        assertThat(response.isNewUser()).isFalse();
        assertThat(response.accessToken()).isNotBlank();

        Member persisted = memberRepository.findByProviderAndProviderId(provider, oidcUserInfo.providerId())
                .orElseThrow();
        assertThat(persisted.getRefreshTokenHash()).isNotBlank();
    }
}