package ru.practicum.ewm.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.ewm.entities.Category;
import ru.practicum.ewm.dtos.CategoryChangedDto;
import ru.practicum.ewm.dtos.CategoryInDto;
import ru.practicum.ewm.dtos.CategoryOutDto;
import ru.practicum.ewm.exceptions.DuplicateObjectException;
import ru.practicum.ewm.mappers.CategoryMapper;
import ru.practicum.ewm.repositories.CategoryRepository;

import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.exceptions.ConditionIsNotMetException;

import ru.practicum.ewm.repositories.EventRepository;

@Service
@Slf4j
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CategoryAdminServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public CategoryOutDto createCategory(CategoryInDto categoryInDto) {

        if (categoryRepository.existsByName(categoryInDto.getName())) {
            throw new DuplicateObjectException(String.format("категория с именем {} уже существует", categoryInDto.getName()));
        }

        Category category = categoryRepository.save(CategoryMapper.toCategory(categoryInDto));
        log.info("новая категория id={}, name={} добавлена", category.getId(), category.getName());
        return CategoryMapper.toCategoryOut(category);
    }

    @Override
    public CategoryOutDto updateCategory(CategoryChangedDto categoryChangedDto) {
        Category category = categoryRepository.findById(categoryChangedDto.getId())
                .orElseThrow(() -> new CategoryNotFoundException(String.format("категория с id=%s не найдена",
                        categoryChangedDto.getId())));
        if (!categoryRepository.existsByName(categoryChangedDto.getName())) {
            category.setName(categoryChangedDto.getName());
            Category updated = categoryRepository.save(category);
            log.info("категория id={}, name={} обновлена", updated.getId(), updated.getName());
            return CategoryMapper.toCategoryOut(updated);
        } else {
            throw new DuplicateObjectException(String.format("категория с именем {} уже существует", categoryChangedDto.getName()));
        }
    }

    @Override
    public void deleteCategory(long catId) {
        if (eventRepository.existsByCategoryId(catId))
            throw new ConditionIsNotMetException("Невозможно удалить категорию из-за привязки " +
                    "минимум одного события к этой категории");

        if (!categoryRepository.existsById(catId))
            throw new CategoryNotFoundException(String.format("категория с id=%s не найдена", catId));

        categoryRepository.deleteById(catId);
        log.info("категория id={} удалена", catId);
    }
}
