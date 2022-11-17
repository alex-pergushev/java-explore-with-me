package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.CategoryChangedDto;
import ru.practicum.ewm.dtos.CategoryInDto;
import ru.practicum.ewm.dtos.CategoryOutDto;

public interface CategoryAdminService {
    CategoryOutDto createCategory(CategoryInDto categoryInDto);

    CategoryOutDto updateCategory(CategoryChangedDto categoryChangedDto);

    void deleteCategory(long catId);
}
