package ru.practicum.ewm.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.event.EventStatClient;
import ru.practicum.ewm.client.event.StatisticEventService;
import ru.practicum.ewm.dtos.CompilationInDto;
import ru.practicum.ewm.dtos.CompilationOutDto;
import ru.practicum.ewm.dtos.EventShortOutDto;
import ru.practicum.ewm.entities.Compilation;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.exceptions.CompilationNotFoundException;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.mappers.CompilationMapper;
import ru.practicum.ewm.repositories.CompilationRepository;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.repositories.RequestRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompilationAdminServiceImpl extends StatisticEventService implements CompilationAdminService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CompilationAdminServiceImpl(EventStatClient eventStatClient, RequestRepository requestRepository,
                                       CompilationRepository compilationRepository, EventRepository eventRepository) {
        super(eventStatClient, requestRepository);
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public CompilationOutDto createCompilation(CompilationInDto compilationInDto) {
        List<Event> events = Arrays.stream(compilationInDto.getEvents())
                .mapToObj(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new EventNotFoundException(String.format("событие с id=%s не найдено", id))))
                .collect(Collectors.toList());
        Compilation compilation = CompilationMapper.toCompilation(compilationInDto, events);
        Compilation saved = compilationRepository.save(compilation);
        log.info("новая подборка id={}, с количеством событий={} успешно добавлена", saved.getId(),
                compilation.getEvents().size());

        return CompilationMapper.toCompilationOut(saved, addConfirmedRequestsAndViews(events, false).stream()
                .map(eventOutDto -> (EventShortOutDto) eventOutDto)
                .collect(Collectors.toList()));
    }

    @Override
    public void deleteCompilation(long compId) {
        if (!compilationRepository.existsById(compId))
            throw new CompilationNotFoundException(String.format("подборка с id=%s не найдена", compId));

        compilationRepository.deleteById(compId);
        log.info("подборка id={} удалена", compId);
    }

    @Override
    public void deleteEvent(long compId, long eventId) {
        Compilation compilation = getCompilationById(compId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("событие с id=%s не найдено", eventId)));

        List<Event> events = new ArrayList<>(compilation.getEvents());
        events.remove(event);
        compilation.setEvents(events);

        compilationRepository.save(compilation);
        log.info("событие id={} удалено из подборки id={}", eventId, compId);
    }

    @Override
    public void addEvent(long compId, long eventId) {
        Compilation compilation = getCompilationById(compId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("событие с id=%s не найдено", eventId)));

        List<Event> events = new ArrayList<>(compilation.getEvents());
        events.add(event);
        compilation.setEvents(events);

        compilationRepository.save(compilation);
        log.info("событие id={} добавлено в подборку id={}", eventId, compId);
    }

    @Override
    public void unpinCompilation(long compId) {
        changePinned(compId, false);
        log.info("подборка id={} откреплена", compId);
    }

    @Override
    public void pinCompilation(long compId) {
        changePinned(compId, true);
        log.info("подборка id={} прикреплена", compId);
    }

    private Compilation getCompilationById(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(String.format("подборка с id=%s не найдена",
                        compId)));
    }

    private void changePinned(long compId, boolean pinned) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(String.format("подборка с id=%s не найдена",
                        compId)));

        compilation.setPinned(pinned);
        compilationRepository.save(compilation);
    }
}
