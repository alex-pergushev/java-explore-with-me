package ru.practicum.ewm.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.entities.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long>, QuerydslPredicateExecutor<Compilation> {

    Page<Compilation> findAllByPinnedFalse(Pageable pageable);

    Page<Compilation> findAllByPinnedTrue(Pageable pageable);
}
