package ru.practicum.ewm.repositories;

import ru.practicum.ewm.entities.ViewWithHits;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepositoryCustom {
    List<ViewWithHits> getViewWithHits(LocalDateTime start, LocalDateTime end, String uri, boolean unique);
}
