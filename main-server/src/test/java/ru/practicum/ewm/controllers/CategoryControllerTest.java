package ru.practicum.ewm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewm.dtos.CategoryOutDto;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.exceptions.ErrorHandler;
import ru.practicum.ewm.services.CategoryService;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private CategoryController categoryController;
    @MockBean
    private CategoryService categoryService;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private static CategoryOutDto categoryOut;

    @BeforeAll
    public static void beforeAll() {
        categoryOut = CategoryOutDto.builder()
                .id(1L)
                .name("Theater")
                .build();
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .setControllerAdvice(new ErrorHandler())
                .build();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getCategoriesStatusIsOk() throws Exception {
        Mockito
                .when(categoryService.getCategories(0, 10))
                .thenReturn(List.of(categoryOut));

        mockMvc.perform(get("/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("Theater"));
    }

    @Test
    void getCategoriesIfFromParamIsNegativeThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/categories?from=-10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Значение from должно быть больше или равна 0"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Ошибка в параметрах URI"))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("BAD_REQUEST"));
    }


    @ParameterizedTest
    @ValueSource(ints = { 0, -100 })
    void getCategoriesIfSizeIsZeroOrNegativeThenStatusIsBadRequest(int value) throws Exception {
        mockMvc.perform(get("/categories?from=10&size=" + value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Минимально допустимое значение для size равно 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Ошибка в параметрах URI"))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("BAD_REQUEST"));
    }

    @Test
    void getCategoryIsOk() throws Exception {
        Mockito
                .when(categoryService.getCategoryById(15))
                .thenReturn(categoryOut);

        mockMvc.perform(get("/categories/15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("Theater"));
    }

    @Test
    void getCategoryIfThrowsNotFoundExceptionThenStatusNotFound() throws Exception {
        Mockito
                .when(categoryService.getCategoryById(15))
                .thenThrow(new CategoryNotFoundException("Категория с id=15 не найдена"));

        mockMvc.perform(get("/categories/15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CategoryNotFoundException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Категория с id=15 не найдена"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Объект не был найден."))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("NOT_FOUND"));
    }

    @Test
    void getCategoryIfCategoryIdIsNegativeThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/categories/-15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Значение {catId} должно быть больше 0"))
                .andExpect(MockMvcResultMatchers.jsonPath("reason")
                        .value("Ошибка в параметрах URI"))
                .andExpect(MockMvcResultMatchers.jsonPath("status")
                        .value("BAD_REQUEST"));
    }
}