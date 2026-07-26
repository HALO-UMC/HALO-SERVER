package com.umc.halo.domain.content.storybook.repository;

import com.umc.halo.domain.content.storybook.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface StorybookRepository extends JpaRepository<Storybook, Long> {
}
