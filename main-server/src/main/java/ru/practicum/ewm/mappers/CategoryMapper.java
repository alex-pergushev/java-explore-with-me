package ru.practicum.ewm.mappers;

import ru.practicum.ewm.entities.Category;
import ru.practicum.ewm.dtos.CategoryInDto;
import ru.practicum.ewm.dtos.CategoryOutDto;

public class CategoryMapper {

    public static CategoryOutDto toCategoryOut(Category category) {
        return CategoryOutDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryInDto category) {
        return Category.builder()
                .name(category.getName())
                .build();
    }
}
