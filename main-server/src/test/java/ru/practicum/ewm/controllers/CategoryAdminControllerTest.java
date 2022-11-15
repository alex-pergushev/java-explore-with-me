package ru.practicum.ewm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.ewm.dtos.CategoryChangedDto;
import ru.practicum.ewm.dtos.CategoryInDto;
import ru.practicum.ewm.dtos.CategoryOutDto;
import ru.practicum.ewm.entities.Category;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.exceptions.ConditionIsNotMetException;
import ru.practicum.ewm.exceptions.ErrorHandler;
import ru.practicum.ewm.services.CategoryAdminService;
import ru.practicum.ewm.utils.TextProcessing;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryAdminController.class)
@AutoConfigureMockMvc
class CategoryAdminControllerTest implements TextProcessing  {

    @Autowired
    private CategoryAdminController categoryAdminController;
    @MockBean
    private CategoryAdminService categoryAdminService;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private static CategoryInDto categoryIn;
    private static CategoryOutDto categoryOut;
    private static CategoryChangedDto changed;

    @BeforeAll
    public static void beforeAll() {
        categoryIn = CategoryInDto.builder()
                .name("Театр")
                .build();

        categoryOut = CategoryOutDto.builder()
                .id(1L)
                .name("Театр")
                .build();

        changed = CategoryChangedDto.builder()
                .id(1L)
                .name("Концерт")
                .build();
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryAdminController)
                .setControllerAdvice(new ErrorHandler())
                .build();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addCategoryStatusIsOk() throws Exception {
        Mockito
                .when(categoryAdminService.createCategory(categoryIn))
                .thenReturn(categoryOut);

        mockMvc.perform(post("/admin/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(categoryIn)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("Театр"));
    }

    @Test
    void addCategoryIfNameIsNullThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(post("/admin/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CategoryInDto())))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Название категории не должно быть пустым"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Ошибка поля в объекте"))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("BAD_REQUEST"));
    }

    @Test
    void addCategoryIfNameTooLongThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(post("/admin/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Category.builder()
                                .name(createText(65)).build())))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Длина названия категории должна быть от 1 до 64 символов"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Ошибка поля в объекте"))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("BAD_REQUEST"));
    }

    @Test
    void updateCategoryStatusIsOk() throws Exception {
        Mockito
                .when(categoryAdminService.updateCategory(changed))
                .thenReturn(categoryOut);

        mockMvc.perform(patch("/admin/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changed)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("Театр"));
    }

    @Test
    void updateCategoryIfThrowsCategoryNotFoundExceptionThenStatusNotFound() throws Exception {
        Mockito
                .when(categoryAdminService.updateCategory(changed))
                .thenThrow(new CategoryNotFoundException("Категория с id=1 не найдена"));

        mockMvc.perform(patch("/admin/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changed)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CategoryNotFoundException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Категория с id=1 не найдена"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Объект не был найден."))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("NOT_FOUND"));
    }

    @Test
    void updateCategoryIfNameIsNullThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(patch("/admin/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CategoryChangedDto())))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Название категории не должно быть пустым"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Ошибка поля в объекте"))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("BAD_REQUEST"));
    }

    @Test
    void updateCategoryIfNameTooLongThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(patch("/admin/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Category.builder()
                                .name(createText(65)).build())))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Длина названия категории должна быть от 1 до 64 символов"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Ошибка поля в объекте"))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("BAD_REQUEST"));
    }

    @Test
    void deleteCategoryStatusIsOk() throws Exception {
        mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategoryIfCategoryIdIsNegativeThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(delete("/admin/categories/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Значение {catId} должно быть больше 0"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Ошибка в параметрах URI"))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("BAD_REQUEST"));
    }

    @Test
    void deleteCategoryIfThrowsCategoryNotFoundExceptionThenStatusIsNotFound() throws Exception {
        Mockito
                .doThrow(new CategoryNotFoundException("Категория с id=1 не найдена"))
                .when(categoryAdminService).deleteCategory(1);

        mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CategoryNotFoundException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Категория с id=1 не найдена"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Объект не был найден."))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("NOT_FOUND"));
    }

    @Test
    void deleteCategoryIfThrowsConditionIsNotMetExceptionThenStatusIsConflict() throws Exception {
        Mockito
                .doThrow(new ConditionIsNotMetException("Невозможно удалить категорию из-за привязки хотя бы одного события " +
                        "к этой категории"))
                .when(categoryAdminService).deleteCategory(1);

        mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConditionIsNotMetException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Невозможно удалить категорию из-за привязки хотя бы одного события " +
                                "к этой категории"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Не соблюдены условия для запрошенной операции."))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("CONFLICT"));
    }
}