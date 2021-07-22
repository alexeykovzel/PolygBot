package com.alexeykovzel.db.repository;

import com.alexeykovzel.db.model.term.Term;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    Term findByValue(String value);

    boolean existsByValue(String value);

    @EntityGraph(attributePaths = {"definitions", "examples"})
    Term findDetailedById(Long id);

    @Query("SELECT t.id FROM Term t WHERE t.value = :value")
    Long findIdByValue(@Param("value") String value);
}
