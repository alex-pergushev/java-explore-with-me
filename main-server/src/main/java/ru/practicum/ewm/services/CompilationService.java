package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.CompilationOutDto;

import java.util.List;

public interface CompilationService {

    List<CompilationOutDto> getCompilations(Boolean pinned, int from, int size);

    CompilationOutDto getCompilationById(long compilationId);
}