package com.umc.halo.domain.notification.repository;

import com.umc.halo.domain.notification.entity.CommonAnniversary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommonAnniversaryRepository extends JpaRepository<CommonAnniversary, Long> {
    List<CommonAnniversary> findByMonthAndDay(Integer month, Integer day);
}
