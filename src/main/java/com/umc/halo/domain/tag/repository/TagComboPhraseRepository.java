package com.umc.halo.domain.tag.repository;

import com.umc.halo.domain.tag.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.Optional;

@Repository
public interface TagComboPhraseRepository extends JpaRepository<TagComboPhrase, Long> {

    Optional<TagComboPhrase> findByTag1AndTag2(Tag tag1, Tag tag2);
}