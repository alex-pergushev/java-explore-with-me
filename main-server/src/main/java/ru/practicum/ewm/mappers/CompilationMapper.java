package ru.practicum.ewm.mappers;

import ru.practicum.ewm.entities.Compilation;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.dtos.CompilationInDto;
import ru.practicum.ewm.dtos.CompilationOutDto;
import ru.practicum.ewm.dtos.EventShortOutDto;

import java.util.List;

public class CompilationMapper {

    public static CompilationOutDto toCompilationOut(Compilation compilation, List<EventShortOutDto> events) {
        return CompilationOutDto.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation toCompilation(CompilationInDto compilation, List<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }
}
