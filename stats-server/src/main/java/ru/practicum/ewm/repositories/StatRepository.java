package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.entities.View;

@EnableJpaRepositories
public interface StatRepository extends JpaRepository<View, Long>, StatRepositoryCustom, QuerydslPredicateExecutor<View> {
}
