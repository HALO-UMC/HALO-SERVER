package com.umc.halo.global.seed;

import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.term.entity.*;
import com.umc.halo.domain.term.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class TermSeeder {
    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public List<Term> seedTerm() {
        List<Term> terms = List.of(
                Term.builder()
                        .title("서비스 이용약관")
                        .shortDescription("HALO 서비스 이용 기준")
                        .description("제1조(목적) 이 약관은 HALO(이하 '회사')가 제공하는 서비스의 이용조건 및 절차, " +
                                "회사와 회원 간의 권리·의무 및 책임사항을 규정함을 목적으로 합니다.\n" +
                                "제2조(용어의 정의) '회원'이란 이 약관에 동의하고 서비스를 이용하는 자를 말합니다.\n" +
                                "제3조(효력 및 변경) 회사는 약관을 변경할 수 있으며, 변경 시 서비스 내 공지사항을 통해 안내합니다.")
                        .isRequired(true)
                        .build(),
                Term.builder()
                        .title("개인정보 처리방침")
                        .shortDescription("수집 항목 보관 기간 안내")
                        .description("회사는 회원가입, 서비스 이용 과정에서 이름, 생년월일, 성별, 소셜 로그인 식별값 등을 수집합니다.\n" +
                                "수집한 개인정보는 서비스 제공 목적으로만 이용되며, 회원 탈퇴 시 관련 법령에서 정한 기간 동안 " +
                                "보관 후 파기됩니다.")
                        .isRequired(true)
                        .build(),
                Term.builder()
                        .title("콘텐츠 보관 및 활용 안내")
                        .shortDescription("사진·기록 저장 기준")
                        .description("회원이 작성한 기록, 사진, 장면카드 등의 콘텐츠는 서비스 제공을 위해 안전하게 저장되며, " +
                                "회원 본인 외에는 공개되지 않습니다.\n" +
                                "회원 탈퇴 시 해당 콘텐츠는 일정 기간 후 삭제됩니다.")
                        .isRequired(true)
                        .build(),
                Term.builder()
                        .title("마케팅 정보 수신 동의")
                        .shortDescription("이벤트·업데이트 안내")
                        .description("이벤트, 신규 기능, 프로모션 등의 정보를 이메일 또는 앱 푸시 알림으로 받아보실 수 있습니다.\n" +
                                "동의하지 않아도 서비스 이용에는 제한이 없으며, 언제든지 설정에서 수신 동의를 철회할 수 있습니다.")
                        .isRequired(false)
                        .build()
        );

        List<Term> savedTerms = termRepository.saveAll(terms);
        log.info("Term {}건 시딩 완료", savedTerms.size());

        return savedTerms;
    }

    @Transactional
    public List<MemberTerm> seedMemberTerm(List<Member> members, List<Term> terms) {
        List<MemberTerm> memberTerms = members.stream()
                .flatMap(member -> terms.stream()
                        .map(term -> MemberTerm.builder()
                                .member(member)
                                .term(term)
                                .isAgreed(term.getIsRequired())
                                .build()))
                .toList();

        List<MemberTerm> savedMemberTerms = memberTermRepository.saveAll(memberTerms);
        log.info("MemberTerm {}건 시딩 완료", savedMemberTerms.size());

        return savedMemberTerms;
    }

    @Transactional
    public void seed() {
        List<Term> terms = seedTerm();

        List<Member> members = memberRepository.findAll().stream()
                .filter(member -> List.of("김하로", "이온").contains(member.getName()))
                .toList();

        seedMemberTerm(members, terms);
    }
}