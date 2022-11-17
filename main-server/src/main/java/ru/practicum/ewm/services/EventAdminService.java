package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.EventAdminChangedDto;
import ru.practicum.ewm.dtos.EventFullOutDto;

import java.util.List;

public interface EventAdminService {

    List<EventFullOutDto> getEvents(int[] users, String[] states, int[] categories, String rangeStart,
                                    String rangeEnd, int from, int size);

    EventFullOutDto updateEvent(long eventId, EventAdminChangedDto eventAdminChangedDto);

    EventFullOutDto publishEvent(long eventId);

    EventFullOutDto rejectEvent(long eventId);
}
