package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.RequestOutDto;

import java.util.List;

public interface RequestService {

    RequestOutDto createRequest(long userId, long eventId);

    RequestOutDto cancelRequest(long userId, long requestId);

    List<RequestOutDto> getRequests(long userId);
}
