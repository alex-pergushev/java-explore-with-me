package ru.practicum.ewm.services;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.repositories.*;
import ru.practicum.ewm.client.event.EventStatClient;
import ru.practicum.ewm.client.event.StatisticEventService;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.enums.Status;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.entities.QEvent;
import ru.practicum.ewm.dtos.EventChangedDto;
import ru.practicum.ewm.dtos.EventFullOutDto;
import ru.practicum.ewm.dtos.EventInDto;
import ru.practicum.ewm.dtos.EventShortOutDto;
import ru.practicum.ewm.mappers.EventMapper;
import ru.practicum.ewm.entities.Request;
import ru.practicum.ewm.dtos.RequestOutDto;
import ru.practicum.ewm.mappers.RequestMapper;
import ru.practicum.ewm.utils.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventPersonalServiceImpl extends StatisticEventService implements EventPersonalService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    @Autowired
    public EventPersonalServiceImpl(EventStatClient eventStatClient, RequestRepository requestRepository,
                                    EventRepository eventRepository, CategoryRepository categoryRepository,
                                    UserRepository userRepository) {
        super(eventStatClient, requestRepository);
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<EventShortOutDto> getEvents(long userId, int from, int size) {
        return addConfirmedRequestsAndViews(eventRepository.findAllByInitiatorId(userId, Pagination.of(from, size))
                .getContent(), false).stream()
                .map(eventOutDto -> (EventShortOutDto) eventOutDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullOutDto getEventById(long userId, long eventId) {
        Event event = getEventOrElseThrow(userId, eventId);

        return (EventFullOutDto) addConfirmedRequestsAndViews(List.of(event), true).get(0);
    }

    @Override
    public EventFullOutDto updateEvent(long userId, EventChangedDto eventChangedDto) {
        Long eventId = eventChangedDto.getId();

        Event beingUpdated = eventId == null ? getEventByInitiatorId(userId)
                : getEventOrElseThrow(userId, eventId);

        Event withChanges = checkChangesAndUpdate(eventChangedDto, beingUpdated);
        Event updated = eventRepository.save(withChanges);
        log.info("событие id={} успешно обновлено", updated.getId());

        return (EventFullOutDto) addConfirmedRequestsAndViews(List.of(updated), true).get(0);
    }

    @Override
    public EventFullOutDto createEvent(long userId, EventInDto eventInDto) {
        long categoryId = eventInDto.getCategory();

        Event newEvent = EventMapper.toEvent(eventInDto, categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("категория с id=%s не найдена",
                        categoryId))), userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("пользователь с id=%s не найден", userId))));

        Event saved = eventRepository.save(newEvent);
        log.info("новое событие добавлено, id={}", newEvent.getId());

        return EventMapper.toEventFull(saved, 0, 0);
    }

    @Override
    public EventFullOutDto cancelEvent(long userId, long eventId) {
        Event event = getEventOrElseThrow(userId, eventId);

        if (event.getState() != State.PENDING)
            throw new ConditionIsNotMetException("Только незаконченные события могут быть отменены");

        event.setState(State.CANCELED);
        Event saved = eventRepository.save(event);
        log.info("статус события id={} изменен на CANCELED", eventId);
        return (EventFullOutDto) addConfirmedRequestsAndViews(List.of(saved), true).get(0);
    }

    @Override
    public List<RequestOutDto> getRequests(long userId, long eventId) {
        existsInitiator(userId, eventId);;

        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toRequestOut)
                .collect(Collectors.toList());
    }

    @Override
    public RequestOutDto confirmRequest(long userId, long eventId, long reqId) {
        Event event = getEventOrElseThrow(userId, eventId);

        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Запрос с id=%s не найден", reqId)));

        if (event.getParticipantLimit() != 0 & event.isRequestModeration()) {
            int limit = event.getParticipantLimit();
            int confirmedRequests = (int) requestRepository.countByEvent(eventId, Status.CONFIRMED);
            if (limit > confirmedRequests) {
                request.setStatus(Status.CONFIRMED);
                requestRepository.save(request);
                log.info("статус запроса id={} изменен на CONFIRMED", reqId);
                if (limit == ++confirmedRequests) {
                    rejectAllPendingRequests(eventId);
                    log.info("все оставшиеся запросы на событие id={} в статусе PENDING были изменены " +
                            "на статус REJECTED", eventId);
                }
            } else throw new ConditionIsNotMetException(String.format("подтверждение запроса с id=%s " +
                    "было отклонено из-за превышения лимита участников", reqId));
        }
        return RequestMapper.toRequestOut(request);
    }

    @Override
    public RequestOutDto rejectRequest(long userId, long eventId, long reqId) {
        existsInitiator(userId, eventId);

        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("запрос с id=%s не найден", reqId)));

        request.setStatus(Status.REJECTED);
        requestRepository.save(request);
        log.info("статус запроса id={} изменен на REJECTED", reqId);

        return RequestMapper.toRequestOut(request);
    }

    private void existsInitiator(long userId, long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new EventNotFoundException(String.format("пользователь с id=%s не инициировал событие с id=%s",
                    userId, eventId));
        }
    }

    private Event getEventOrElseThrow(long userId, long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException(String.format("пользователь с id=%s не инициировал" +
                        "событие с id=%s", userId, eventId)));
    }

    private Event getEventByInitiatorId(long userId) {
        QEvent event = QEvent.event;
        BooleanExpression condition = event.initiator.id.eq(userId).and(event.state.in(State.PENDING, State.CANCELED));
        Iterable<Event> events = eventRepository.findAll(condition);
        if (!events.iterator().hasNext())
            throw new ConditionIsNotMetException("не было найдено ни одного события со статусом на рассмотрении или отменен");
        if (events.spliterator().getExactSizeIfKnown() > 1) {
            throw new ConditionIsNotMetException("не было найдено ни одного события со статусом на рассмотрении или отменен");
        }
        return events.iterator().next();
    }

    private Event checkChangesAndUpdate(EventChangedDto changed, Event beingUpdated) {
        if (beingUpdated.getState() != State.CANCELED && beingUpdated.getState() != State.PENDING)
            throw new ConditionIsNotMetException("Могут быть изменены только события со статусом на рассмотрении или отменены");
        if (changed.getEventDate() != null) beingUpdated.setEventDate(changed.getEventDate());
        if (changed.getAnnotation() != null) beingUpdated.setAnnotation(changed.getAnnotation());
        if (changed.getDescription() != null) beingUpdated.setDescription(changed.getDescription());
        if (changed.getTitle() != null) beingUpdated.setTitle(changed.getTitle());
        if (changed.getCategory() != null && !changed.getCategory().equals(beingUpdated.getCategory().getId())) {
            long catId = changed.getCategory();
            beingUpdated.setCategory(categoryRepository.findById(catId)
                    .orElseThrow(() -> new CategoryNotFoundException(String.format("категория с id=%s не найдена",
                            catId))));
        }
        if (changed.getParticipantLimit() != null) beingUpdated
                .setParticipantLimit(changed.getParticipantLimit());
        if (changed.getPaid() != null) beingUpdated.setPaid(changed.getPaid());
        if (beingUpdated.getState() == State.CANCELED) beingUpdated.setState(State.PENDING);
        return beingUpdated;
    }

    private void rejectAllPendingRequests(long eventId) {
        requestRepository.findAllByEventId(eventId).stream()
                .filter(request -> request.getStatus() == Status.PENDING)
                .forEach(request -> {
                    request.setStatus(Status.REJECTED);
                    requestRepository.save(request);
                });
    }
}
