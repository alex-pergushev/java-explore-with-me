package ru.practicum.ewm.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.RequestOutDto;
import ru.practicum.ewm.exceptions.ConditionIsNotMetException;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.RequestNotFoundException;
import ru.practicum.ewm.exceptions.UserNotFoundException;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.enums.Status;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.entities.Request;
import ru.practicum.ewm.mappers.RequestMapper;
import ru.practicum.ewm.repositories.RequestRepository;
import ru.practicum.ewm.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    @Override
    public RequestOutDto createRequest(long userId, long eventId) {
        if (requestRepository.existsByRequester(userId, eventId))
            throw new ConditionIsNotMetException(String.format("пользователь с id=%s уже напралял запрос на участие " +
                    "в событии с id=%s", userId, eventId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("событие с id=%s не найдено", eventId)));
        checkEventForRestrictions(event, userId);

        Request request = Request.builder()
                .requester(userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(String.format("пользователь с id=%s не найден", userId))))
                .event(event)
                .status(event.isRequestModeration() ? Status.PENDING : Status.CONFIRMED)
                .created(LocalDateTime.now())
                .build();

        Request saved = requestRepository.save(request);
        log.info("запрос id={} успешно добавлен со статусом-{}", saved.getId(), saved.getStatus());
        return RequestMapper.toRequestOut(saved);
    }

    @Override
    public RequestOutDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("запрос с id=%s не найден", requestId)));

        if (request.getRequester().getId() != userId)
            throw new ConditionIsNotMetException("запрос принадлежит другому пользователю");

        request.setStatus(Status.CANCELED);
        Request updated = requestRepository.save(request);
        log.info("статус запроса id={} изменен на CANCELED", requestId);

        return RequestMapper.toRequestOut(updated);
    }

    @Override
    public List<RequestOutDto> getRequests(long userId) {
        return requestRepository.findAllByRequester(userId).stream()
                .map(RequestMapper::toRequestOut)
                .collect(Collectors.toList());
    }

    private void checkEventForRestrictions(Event event, long userId) {
        if (event.getInitiator().getId() == userId)
            throw new ConditionIsNotMetException("Инициатор события не может создать заявку на участие " +
                    "в своем собственном событии");

        if (event.getState() != State.PUBLISHED)
            throw new ConditionIsNotMetException("нельзя подать заявку на участие в неопубликованном событии");

        int limit = event.getParticipantLimit();

        if (limit > 0) {
            int confirmedRequests = (int) requestRepository.countByEvent(event.getId(), Status.CONFIRMED);
            if (confirmedRequests == limit)
                throw new ConditionIsNotMetException(String.format("событие с id=%s уже достигло предела " +
                        "запросов на участие в нем", event.getId()));
        }

        log.info("событие id={} не имеет ограничений для запроса на участие в нем", event.getId());
    }
}
