package com.umc.halo.domain.member.repository;

import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.enums.Provider;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

    boolean existsByName(String name);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select m
        from Member m
        where m.provider = :provider
        and m.providerId = :providerId
    """)
    Optional<Member> findByProviderAndProviderIdForUpdate(@Param("provider") Provider provider, @Param("providerId") String providerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select m
        from Member m
        where m.id = :id
       """)
    Optional<Member> findByIdForUpdate(@Param("id") Long id);

    @Query("""
        select m.id
        from Member m
    """)
    List<Long> findAllIds();
}
