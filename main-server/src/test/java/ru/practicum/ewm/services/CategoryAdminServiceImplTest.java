package ru.practicum.ewm.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.dtos.CategoryChangedDto;
import ru.practicum.ewm.dtos.CategoryInDto;
import ru.practicum.ewm.dtos.CategoryOutDto;
import ru.practicum.ewm.entities.Category;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.exceptions.ConditionIsNotMetException;
import ru.practicum.ewm.repositories.CategoryRepository;
import ru.practicum.ewm.repositories.EventRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryAdminServiceImplTest {
    @InjectMocks
    private CategoryAdminServiceImpl categoryAdminService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventRepository eventRepository;

    private static CategoryInDto categoryIn;
    private static CategoryChangedDto changed;
    private static Category category;

    @BeforeAll
    public static void beforeAll() {
        categoryIn = CategoryInDto.builder()
                .name("Theater")
                .build();

        changed = CategoryChangedDto.builder()
                .id(5L)
                .name("Concerts")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Concerts")
                .build();
    }

    @Test
    void whenCreateCategoryThenCallSaveRepository() {
        Mockito.when(categoryRepository.save(Mockito.any(Category.class)))
                .thenReturn(category);

        CategoryOutDto returned = categoryAdminService.createCategory(categoryIn);

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(category.getId()));
        assertThat(returned.getName(), equalTo(category.getName()));

        Mockito.verify(categoryRepository, Mockito.times(1))
                .save(Mockito.any(Category.class));
    }

    @Test
    void whenUpdateCategoryIfCategoryExistsThenCallSaveRepository() {
        Mockito.when(categoryRepository.findById(5L))
                .thenReturn(Optional.of(category));

        Mockito.when(categoryRepository.save(Mockito.any(Category.class)))
                .thenReturn(category);

        CategoryOutDto returned = categoryAdminService.updateCategory(changed);

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(category.getId()));
        assertThat(returned.getName(), equalTo(category.getName()));

        Mockito.verify(categoryRepository, Mockito.times(1))
                .findById(5L);

        Mockito.verify(categoryRepository, Mockito.times(1))
                .save(Mockito.any(Category.class));
    }

    @Test
    void whenUpdateCategoryIfCategoryNotExistsThenThrowsCategoryNotFoundException() {
        Mockito.when(categoryRepository.findById(5L))
                .thenReturn(Optional.empty());

        final CategoryNotFoundException exception = Assertions.assertThrows(
                CategoryNotFoundException.class,
                () -> categoryAdminService.updateCategory(changed));

        Assertions.assertEquals("категория с id=5 не найдена", exception.getMessage());

        Mockito.verify(categoryRepository, Mockito.times(1))
                .findById(5L);

        Mockito.verify(categoryRepository, Mockito.never())
                .save(Mockito.any(Category.class));
    }

    @Test
    void whenDeleteCategoryIfEventExistsWithThisCategoryThenThrowsConditionIsNotMetException() {
        Mockito.when(eventRepository.existsByCategoryId(5))
                .thenReturn(true);

        final ConditionIsNotMetException exception = Assertions.assertThrows(
                ConditionIsNotMetException.class,
                () -> categoryAdminService.deleteCategory(5));

        Assertions.assertEquals("Невозможно удалить категорию из-за привязки минимум одного события " +
                "к этой категории", exception.getMessage());

        Mockito.verify(eventRepository, Mockito.times(1))
                .existsByCategoryId(5);

        Mockito.verify(categoryRepository, Mockito.never())
                .existsById(Mockito.anyLong());

        Mockito.verify(categoryRepository, Mockito.never())
                .deleteById(Mockito.anyLong());
    }

    @Test
    void whenDeleteCategoryIfItNotExistsThenThrowsCategoryNotFoundException() {
        Mockito.when(eventRepository.existsByCategoryId(5))
                .thenReturn(false);

        Mockito.when(categoryRepository.existsById(5L))
                .thenReturn(false);

        final CategoryNotFoundException exception = Assertions.assertThrows(
                CategoryNotFoundException.class,
                () -> categoryAdminService.deleteCategory(5));

        Assertions.assertEquals("категория с id=5 не найдена", exception.getMessage());

        Mockito.verify(eventRepository, Mockito.times(1))
                .existsByCategoryId(5);

        Mockito.verify(categoryRepository, Mockito.times(1))
                .existsById(Mockito.anyLong());

        Mockito.verify(categoryRepository, Mockito.never())
                .deleteById(Mockito.anyLong());
    }

    @Test
    void whenDeleteCategoryThenCallDeleteRepository() {
        Mockito.when(eventRepository.existsByCategoryId(5))
                .thenReturn(false);

        Mockito.when(categoryRepository.existsById(5L))
                .thenReturn(true);

        categoryAdminService.deleteCategory(5);

        Mockito.verify(eventRepository, Mockito.times(1))
                .existsByCategoryId(5);

        Mockito.verify(categoryRepository, Mockito.times(1))
                .existsById(5L);

        Mockito.verify(categoryRepository, Mockito.times(1))
                .deleteById(5L);
    }
}