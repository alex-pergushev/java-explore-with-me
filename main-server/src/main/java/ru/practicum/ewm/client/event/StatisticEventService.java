package ru.practicum.ewm.client.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.enums.Status;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.dtos.EventFullOutDto;
import ru.practicum.ewm.dtos.EventOutDto;
import ru.practicum.ewm.dtos.EventShortOutDto;
import ru.practicum.ewm.mappers.EventMapper;
import ru.practicum.ewm.repositories.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public abstract class StatisticEventService {
    private final EventStatClient eventStatClient;
    protected final RequestRepository requestRepository;

    protected List<EventOutDto> addConfirmedRequestsAndViews(List<Event> events, boolean isFull) {

       if (events.isEmpty()) {
            log.info("передан пустой список событий");
            return new ArrayList<>();
        }

        Map<Long, Long> eventViews = eventStatClient.getStatisticOnViews(events, true);
        log.debug("статистика по событиям, количество просмотров={}", eventViews.size());

        AtomicLong hits = new AtomicLong();
        return events.stream()
                .peek(event -> hits.set(eventViews.get(event.getId()) == null ? 0 : eventViews.get(event.getId())))
                .map(event -> isFull ? mapToEventFull(event, hits.get()) : mapToEventShort(event, hits.get()))
                .collect(Collectors.toList());
    }

    private EventShortOutDto mapToEventShort(Event event, long views) {
        return EventMapper.toEventShort(event, event.isRequestModeration() ?
                (int) requestRepository.countByEvent(event.getId(), Status.CONFIRMED)
                : (int) requestRepository.countByEventId(event.getId()), views);
    }

    private EventFullOutDto mapToEventFull(Event event, long views) {
        return EventMapper.toEventFull(event, event.isRequestModeration() ?
                (int) requestRepository.countByEvent(event.getId(), Status.CONFIRMED)
                : (int) requestRepository.countByEventId(event.getId()), views);
    }
}
