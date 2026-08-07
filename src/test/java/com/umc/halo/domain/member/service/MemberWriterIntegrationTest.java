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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
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
}