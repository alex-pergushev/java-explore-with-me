package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.CompilationInDto;
import ru.practicum.ewm.dtos.CompilationOutDto;

public interface CompilationAdminService {

    CompilationOutDto createCompilation(CompilationInDto compilationInDto);

    void deleteCompilation(long compId);

    void deleteEvent(long compId, long eventId);

    void addEvent(long compId, long eventId);

    void unpinCompilation(long compId);

    void pinCompilation(long compId);
}
