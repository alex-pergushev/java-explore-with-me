package ru.practicum.ewm.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.CompilationOutDto;
import ru.practicum.ewm.services.CompilationService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping("/compilations")
public class CompilationController {
    private final CompilationService compilationService;

    public CompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    public List<CompilationOutDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(value = "from", defaultValue = "0")
                                                   @PositiveOrZero(message = "Значение from должно быть больше " +
                                                           "или равна 0") int from,
                                                   @RequestParam(value = "size", defaultValue = "10")
                                                       @Min(value = 1, message = "Минимально допустимое значение " +
                                                               "для size равно 1")
                                                       int size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationOutDto getCompilation(@PathVariable @Positive(message = "Значение {compId} должно быть больше 0")
                                                      int compId) {
        return compilationService.getCompilationById(compId);
    }
}
