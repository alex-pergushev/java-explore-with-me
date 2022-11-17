package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.entities.User;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User>  {
    boolean existsByName(String name);
}
