package com.umc.halo.domain.member.repository;

import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.enums.Provider;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

    boolean existsByName(String name);
}
