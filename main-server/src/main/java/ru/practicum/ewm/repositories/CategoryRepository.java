package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.entities.Category;

@EnableJpaRepositories
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
