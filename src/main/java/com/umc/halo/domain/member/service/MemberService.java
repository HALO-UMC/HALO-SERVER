package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
}
