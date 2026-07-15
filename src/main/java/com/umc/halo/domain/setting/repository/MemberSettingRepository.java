package com.umc.halo.domain.setting.repository;

import com.umc.halo.domain.setting.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface MemberSettingRepository extends JpaRepository<MemberSetting, Long> {

    void deleteByMemberId(Long memberId);
}
