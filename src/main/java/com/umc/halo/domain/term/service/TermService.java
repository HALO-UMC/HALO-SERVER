package com.umc.halo.domain.term.service;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.term.converter.TermConverter;
import com.umc.halo.domain.term.dto.TermReqDTO;
import com.umc.halo.domain.term.dto.TermResDTO;
import com.umc.halo.domain.term.entity.MemberTerm;
import com.umc.halo.domain.term.entity.Term;
import com.umc.halo.domain.term.exception.TermException;
import com.umc.halo.domain.term.exception.code.TermErrorCode;
import com.umc.halo.domain.term.repository.MemberTermRepository;
import com.umc.halo.domain.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TermService {

    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;

    // 약관 목록 조회
    @Transactional(readOnly = true)
    public List<TermResDTO.Info> getTerms() {
        return termRepository.findAllByOrderByIdAsc().stream()
                .map(TermConverter::toInfo)
                .toList();
    }

    // 약관 동의 처리
    @Transactional
    public void agree(Long memberId, TermReqDTO.Agree request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<TermReqDTO.Agreement> agreements =
                (request == null || request.agreements() == null) ? List.of() : request.agreements();

        Map<Long, Boolean> requested = new HashMap<>();
        for (TermReqDTO.Agreement agreement : agreements) {
            requested.put(agreement.termId(), Boolean.TRUE.equals(agreement.isAgreed()));
        }

        List<Term> terms = termRepository.findAllByOrderByIdAsc();
        Map<Long, Term> termMap = terms.stream()
                .collect(Collectors.toMap(Term::getId, term -> term));

        validateTermsExist(requested.keySet(), termMap);
        validateRequiredAgreed(terms, requested);

        saveAgreements(member, requested, termMap);
    }

    private void validateTermsExist(Set<Long> requestedTermIds, Map<Long, Term> termMap) {

        for (Long termId : requestedTermIds) {
            if (termId == null || !termMap.containsKey(termId)) {
                throw new TermException(TermErrorCode.NOT_FOUND);
            }
        }
    }

    private void validateRequiredAgreed(List<Term> terms, Map<Long, Boolean> requested) {

        for (Term term : terms) {
            if (Boolean.TRUE.equals(term.getIsRequired())
                    && !Boolean.TRUE.equals(requested.get(term.getId()))) {
                throw new TermException(TermErrorCode.REQUIRED_NOT_AGREED);
            }
        }
    }

    private void saveAgreements(Member member, Map<Long, Boolean> requested, Map<Long, Term> termMap) {

        Map<Long, MemberTerm> existing = memberTermRepository.findAllByMember(member).stream()
                .collect(Collectors.toMap(
                        memberTerm -> memberTerm.getTerm().getId(),
                        memberTerm -> memberTerm,
                        (first, second) -> first));

        for (Map.Entry<Long, Boolean> entry : requested.entrySet()) {
            MemberTerm memberTerm = existing.get(entry.getKey());

            if (memberTerm != null) {
                memberTerm.updateIsAgreed(entry.getValue());
            } else {
                memberTermRepository.save(MemberTerm.builder()
                        .member(member)
                        .term(termMap.get(entry.getKey()))
                        .isAgreed(entry.getValue())
                        .build());
            }
        }
    }

    @Transactional(readOnly = true)
    public TermResDTO.AgreementStatus getAgreementStatus(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        Set<Long> agreedTermIds = memberTermRepository.findAllByMember(member).stream()
                .filter(memberTerm -> Boolean.TRUE.equals(memberTerm.getIsAgreed()))
                .map(memberTerm -> memberTerm.getTerm().getId())
                .collect(Collectors.toSet());

        boolean termsAgreed = termRepository.findAll().stream()
                .filter(term -> Boolean.TRUE.equals(term.getIsRequired()))
                .allMatch(term -> agreedTermIds.contains(term.getId()));

        return TermConverter.toAgreementStatus(termsAgreed);
    }
}