package com.umc.halo.domain.content.storybook.repository;

import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.content.storybook.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface StorybookCharacterRepository extends JpaRepository<StorybookCharacter, Long> {

    Optional<StorybookCharacter> findByStorybookAndVariant(Storybook storybook, Variant variant);
}
