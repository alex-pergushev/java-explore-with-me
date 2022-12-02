package ru.practicum.ewm.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.entities.Comment;
import ru.practicum.ewm.enums.State;

import java.util.List;

@EnableJpaRepositories
public interface CommentRepository extends JpaRepository<Comment, Long> {


    Page<Comment> findAllByAuthorId(long userId, Pageable pageable);

    List<Comment> findAllByEventIdAndState(Long eventId, State status);


}
