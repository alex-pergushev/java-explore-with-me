package ru.practicum.ewm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.*;
import ru.practicum.ewm.services.EventPersonalService;
import ru.practicum.ewm.dtos.RequestOutDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping("/users/{userId}/events")
public class EventPersonalController {
    private final EventPersonalService eventPersonalService;

    @Autowired
    public EventPersonalController(EventPersonalService eventPersonalService) {
        this.eventPersonalService = eventPersonalService;
    }

    @GetMapping
    public List<EventShortOutDto> getEvents(@PathVariable @Positive(message = "Значение userId должно быть больше 0")
                                            long userId,
                                            @RequestParam(value = "from", defaultValue = "0")
                                            @PositiveOrZero(message = "Значение from должно быть больше или равно 0")
                                            int from,
                                            @RequestParam(value = "size", defaultValue = "10")
                                            @Min(value = 1, message = "Минимально допустимое значение для size равно 1")
                                            int size) {
        return eventPersonalService.getEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullOutDto getEvent(@PathVariable @Positive(message = "Значение {userId} должно быть больше 0")
                                    long userId,
                                    @PathVariable @Positive(message = "Значение {eventId} должно быть больше 0")
                                    long eventId) {
        return eventPersonalService.getEventById(userId, eventId);
    }

    @PatchMapping
    public EventFullOutDto updateEvent(@PathVariable @Positive(message = "Значение userId должно быть больше 0") long userId,
                                       @RequestBody @Valid EventChangedDto eventChangedDto) {
        return eventPersonalService.updateEvent(userId, eventChangedDto);
    }

    @PostMapping
    public EventFullOutDto addEvent(@PathVariable @Positive(message = "Значение userId должно быть больше 0") long userId,
                                    @RequestBody @Valid EventInDto eventInDto) {
        return eventPersonalService.createEvent(userId, eventInDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullOutDto cancelEvent(@PathVariable @Positive(message = "Значение {userId} должно быть больше 0")
                                       long userId,
                                       @PathVariable @Positive(message = "Значение {eventId} должно быть больше 0")
                                       long eventId) {
        return eventPersonalService.cancelEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestOutDto> getRequests(@PathVariable @Positive(message = "Значение {userId} должно быть больше 0")
                                           long userId,
                                           @PathVariable @Positive(message = "Значение {eventId} должно быть больше 0")
                                           long eventId) {
        return eventPersonalService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public RequestOutDto confirmRequest(@PathVariable @Positive(message = "Значение {userId} должно быть больше 0")
                                        long userId,
                                        @PathVariable @Positive(message = "Значение {eventId} должно быть больше 0")
                                        long eventId,
                                        @PathVariable @Positive(message = "Значение {reqId} должно быть больше 0")
                                        long reqId) {
        return eventPersonalService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public RequestOutDto rejectRequest(@PathVariable @Positive(message = "Значение {userId} должно быть больше 0")
                                       long userId,
                                       @PathVariable @Positive(message = "Значение {eventId} должно быть больше 0")
                                       long eventId,
                                       @PathVariable @Positive(message = "Значение {reqId} должно быть больше 0")
                                       long reqId) {
        return eventPersonalService.rejectRequest(userId, eventId, reqId);
    }
}
