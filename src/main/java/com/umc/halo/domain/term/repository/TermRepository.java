package com.umc.halo.domain.term.repository;

import com.umc.halo.domain.term.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {

    List<Term> findAllByOrderByIdAsc();
}
