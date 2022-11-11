package ru.practicum.ewm.mappers;

import ru.practicum.ewm.entities.Request;
import ru.practicum.ewm.dtos.RequestOutDto;

public class RequestMapper {
    public static RequestOutDto toRequestOut(Request request) {
        return RequestOutDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }
}
