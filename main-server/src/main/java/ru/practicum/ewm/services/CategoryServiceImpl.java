package ru.practicum.ewm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.CategoryOutDto;
import ru.practicum.ewm.mappers.CategoryMapper;
import ru.practicum.ewm.repositories.CategoryRepository;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.utils.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    @Override
    public List<CategoryOutDto> getCategories(int from, int size) {
        return categoryRepository.findAll(Pagination.of(from, size)).getContent().stream()
                .map(CategoryMapper::toCategoryOut)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryOutDto getCategoryById(long catId) {
        return CategoryMapper.toCategoryOut(categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(
                        String.format("категория с id=%s не найдена", catId))));
    }
}
