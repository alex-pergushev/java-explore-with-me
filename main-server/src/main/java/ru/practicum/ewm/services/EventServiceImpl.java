package ru.practicum.ewm.services;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.event.EventStatClient;
import ru.practicum.ewm.client.event.StatisticEventService;
import ru.practicum.ewm.entities.Comment;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.NoAccessRightsException;
import ru.practicum.ewm.enums.SortingEvents;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.enums.Status;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.entities.QEvent;
import ru.practicum.ewm.dtos.EventFullOutDto;
import ru.practicum.ewm.dtos.EventShortOutDto;
import ru.practicum.ewm.mappers.CommentMapper;
import ru.practicum.ewm.mappers.EventMapper;
import ru.practicum.ewm.repositories.CommentRepository;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.entities.QRequest;
import ru.practicum.ewm.repositories.RequestRepository;
import ru.practicum.ewm.utils.Pagination;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventServiceImpl extends StatisticEventService implements EventService {
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public EventServiceImpl(EventStatClient eventStatClient, RequestRepository requestRepository, EventRepository eventRepository, CommentRepository commentRepository) {
        super(eventStatClient, requestRepository);
        this.eventRepository = eventRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<EventShortOutDto> getEvents(String text, int[] categories, Boolean paid, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, boolean onlyAvailable, String sort,
                                            int from, int size) {
        BooleanExpression finalCondition = getFinalCondition(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable);
        log.debug("окончательное условие было успешно сформировано: {}", finalCondition);

        if (SortingEvents.valueOf(sort) == SortingEvents.EVENT_DATE) return getEventsSortedByDate(finalCondition, from,
                size);

        return getEventsSortedByViews(finalCondition, from, size);
    }

   @Override
    public EventFullOutDto getEventById(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(String.format("событие с id=%s не найдено", id)));

        if (event.getState() != State.PUBLISHED)
            throw new NoAccessRightsException(String.format("Нет прав на просмотр события с id=%s потому что " +
                    "оно еще не была опубликовано", id));

        List<Comment> comments = commentRepository.findAllByEventIdAndState(event.getId(), State.PUBLISHED);
        EventFullOutDto eventFullDto = EventMapper.toEventFull(event,0,0);
        eventFullDto.setComments(comments.stream().map(CommentMapper::toCommentOut).collect(Collectors.toList()));

       EventFullOutDto result = (EventFullOutDto) addConfirmedRequestsAndViews(List.of(event), true).get(0);

        return eventFullDto;
    }

    private BooleanExpression getFinalCondition(String text, int[] categories, Boolean paid, LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd, boolean onlyAvailable) {
        List<BooleanExpression> conditions = new ArrayList<>();
        QEvent event = QEvent.event;

        conditions.add(event.state.eq(State.PUBLISHED).and(event.eventDate.after(rangeStart == null ? LocalDateTime.now()
                : rangeStart)));
        if (text != null)
            conditions.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        if (categories != null) {
            List<Long> catIds = Arrays.stream(categories).mapToObj(Long::valueOf).collect(Collectors.toList());
            conditions.add(event.category.id.in(catIds));
        }
        if (paid != null) conditions.add(paid ? event.paid.isTrue() : event.paid.isFalse());
        if (rangeEnd != null) conditions.add(event.eventDate.before(rangeEnd));
        if (onlyAvailable) {
            QRequest request = QRequest.request;
            BooleanExpression ifLimitIsZero = event.participantLimit.eq(0);
            BooleanExpression ifRequestModerationFalse = event.requestModeration.isFalse()
                    .and(event.participantLimit.goe(request.count()));
            BooleanExpression ifRequestModerationTrue = event.requestModeration.isTrue()
                    .and(event.participantLimit.goe(request.status.eq(Status.CONFIRMED).count()));
            conditions.add(ifLimitIsZero.or(ifRequestModerationFalse).or(ifRequestModerationTrue));
        }

        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    private List<EventShortOutDto> getEventsSortedByDate(BooleanExpression expression, int from, int size) {
        Pageable pageable = Pagination.of(from, size, Sort.by("eventDate").descending());
        Slice<Event> events = eventRepository.findAll(expression, pageable);

        return addConfirmedRequestsAndViews(events.getContent(), false).stream()
                .map(eventOutDto -> (EventShortOutDto) eventOutDto)
                .collect(Collectors.toList());
    }

    private List<EventShortOutDto> getEventsSortedByViews(BooleanExpression expression, int from, int size) {
        List<Event> events = new ArrayList<>();
        eventRepository.findAll(expression).forEach(events::add);

        return addConfirmedRequestsAndViews(events, false).stream()
                .map(eventOutDto -> (EventShortOutDto) eventOutDto)
                .sorted(Comparator.comparing(EventShortOutDto::getViews))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }
}
