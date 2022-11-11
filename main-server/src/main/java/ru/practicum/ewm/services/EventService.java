package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.EventFullOutDto;
import ru.practicum.ewm.dtos.EventShortOutDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortOutDto> getEvents(String text, int[] categories, Boolean paid, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, boolean onlyAvailable, String sortingEvents,
                                     int from, int size);

    EventFullOutDto getEventById(long id);
}
