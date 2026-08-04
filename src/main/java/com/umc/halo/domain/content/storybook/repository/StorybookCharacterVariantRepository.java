package com.umc.halo.domain.content.storybook.repository;

import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.entity.StorybookCharacter;
import com.umc.halo.domain.content.storybook.entity.StorybookCharacterVariant;
import com.umc.halo.domain.content.storybook.enums.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorybookCharacterVariantRepository extends JpaRepository<StorybookCharacterVariant, Long> {

    Optional<StorybookCharacterVariant> findByStorybookCharacter_StorybookAndVariant(Storybook storybook, Variant variant);

    @Query("""
                    SELECT sv FROM StorybookCharacterVariant sv
                    JOIN FETCH sv.storybookCharacter sc
                    JOIN FETCH sc.storybook s
                    WHERE sc.storybook IN :storybooks AND sv.variant=:variant
            """)
    List<StorybookCharacterVariant> findAllByStorybookCharacter_StorybookInAndVariant(
            @Param("storybooks") List<Storybook> storybooks,
            @Param("variant") Variant variant);

    Optional<StorybookCharacterVariant> findByStorybookCharacterAndVariant(StorybookCharacter storybookCharacter, Variant variant);
}
