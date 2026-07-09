package com.umc.halo.domain.member.repository;

import com.umc.halo.domain.member.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
