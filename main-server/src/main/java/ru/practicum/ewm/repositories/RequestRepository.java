package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.enums.Status;
import ru.practicum.ewm.entities.Request;

import java.util.List;

@EnableJpaRepositories
public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {

    long countByEventId(long eventId);

    @Query("select count(r) from Request r where r.event.id = :eventId and r.status = :status")
    long countByEvent(long eventId, Status status);

    List<Request> findAllByEventId(long eventId);

    @Query("select r from Request r where r.requester.id = :userId")
    List<Request> findAllByRequester(long userId);

    @Query("select (count(r) > 0) from Request r where r.requester.id = :userId and r.event.id = :eventId")
    boolean existsByRequester(long userId, long eventId);

}
