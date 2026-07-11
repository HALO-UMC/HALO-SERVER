package com.umc.halo.domain.notification.repository;

import com.umc.halo.domain.notification.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface AnniversaryRepository extends JpaRepository<Anniversary, Long> {
}
