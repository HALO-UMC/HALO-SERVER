package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.dto.request.LoginRequestDTO;
import com.umc.halo.domain.member.dto.response.LoginResponseDTO;
import com.umc.halo.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public LoginResponseDTO.LoginResponse login(LoginRequestDTO.Login dto) {
        return null;
    }
}
