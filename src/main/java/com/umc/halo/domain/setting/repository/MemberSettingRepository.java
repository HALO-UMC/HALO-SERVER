package com.umc.halo.domain.setting.repository;

import com.umc.halo.domain.setting.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberSettingRepository extends JpaRepository<MemberSetting, Long> {

    void deleteByMemberId(Long memberId);
    Optional<MemberSetting> findByMemberId(Long memberId);
    @Query("""
        select ms
        from MemberSetting ms
        join fetch ms.member
    """)
    List<MemberSetting> findAllWithMember();
}
