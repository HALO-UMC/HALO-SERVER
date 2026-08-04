package com.umc.halo.domain.member.repository;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.entity.MemberDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberDeviceRepository extends JpaRepository<MemberDevice, Long> {
    Optional<MemberDevice> findByMemberAndDeviceIdentifier(Member member, String deviceIdentifier);
    List<MemberDevice> findAllByMember(Member member);
    void deleteByMemberId(Long memberId);
}
