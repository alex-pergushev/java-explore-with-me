package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.CategoryOutDto;

import java.util.List;

public interface CategoryService {
    List<CategoryOutDto> getCategories(int from, int size);
    CategoryOutDto getCategoryById(long catId);
}
