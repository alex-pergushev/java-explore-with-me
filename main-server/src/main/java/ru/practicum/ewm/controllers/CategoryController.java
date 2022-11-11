package ru.practicum.ewm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.ewm.dtos.CategoryOutDto;
import ru.practicum.ewm.services.CategoryService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryOutDto> getCategories(@RequestParam(value = "from", defaultValue = "0")
                                              @PositiveOrZero(message = "Значение from должно быть больше или равна 0")
                                              int from,
                                              @RequestParam(value = "size", defaultValue = "10")
                                              @Min(value = 1, message = "Минимально допустимое значение для size равно 1")
                                              int size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryOutDto getCategory(@PathVariable @Positive(message = "Значение {catId} должно быть больше 0") long catId) {
        return categoryService.getCategoryById(catId);
    }
}
