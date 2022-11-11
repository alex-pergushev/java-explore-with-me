package ru.practicum.ewm.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.services.CompilationAdminService;
import ru.practicum.ewm.dtos.CompilationInDto;
import ru.practicum.ewm.dtos.CompilationOutDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@Validated
@RequestMapping("/admin/compilations")
public class CompilationAdminController {
    private final CompilationAdminService compilationAdminService;

    public CompilationAdminController(CompilationAdminService compilationAdminService) {
        this.compilationAdminService = compilationAdminService;
    }

    @PostMapping
    public CompilationOutDto addCompilation(@RequestBody @Valid CompilationInDto compilationInDto) {
        return compilationAdminService.createCompilation(compilationInDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable @Positive(message = "Значение {compId} должно быть больше 0") long compId) {
        compilationAdminService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable @Positive(message = "Значение {compId} должно быть больше 0") long compId,
                            @PathVariable @Positive(message = "Значение {eventId} должно быть больше 0")
                            long eventId) {
        compilationAdminService.deleteEvent(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEvent(@PathVariable @Positive(message = "Значение {compId} должно быть больше 0") long compId,
                         @PathVariable @Positive(message = "Значение {eventId} должно быть больше 0") long eventId) {
        compilationAdminService.addEvent(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpinCompilation(@PathVariable @Positive(message = "Значение {compId} должно быть больше 0") long compId) {
        compilationAdminService.unpinCompilation(compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilation(@PathVariable @Positive(message = "Значение {compId} должно быть больше 0") long compId) {
        compilationAdminService.pinCompilation(compId);
    }
}
