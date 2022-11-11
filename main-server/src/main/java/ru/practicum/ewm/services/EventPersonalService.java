package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.*;

import java.util.List;

public interface EventPersonalService {

    List<EventShortOutDto> getEvents(long userId, int from, int size);

    EventFullOutDto getEventById(long userId, long id);

    EventFullOutDto updateEvent(long userId, EventChangedDto eventChangedDto);

    EventFullOutDto createEvent(long userId, EventInDto eventInDto);

    EventFullOutDto cancelEvent(long userId, long eventId);

    List<RequestOutDto> getRequests(long userId, long eventId);

    RequestOutDto confirmRequest(long userId, long eventId, long reqId);

    RequestOutDto rejectRequest(long userId, long eventId, long reqId);
}