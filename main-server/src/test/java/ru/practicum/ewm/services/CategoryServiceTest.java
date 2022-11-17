package ru.practicum.ewm.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dtos.CategoryInDto;
import ru.practicum.ewm.dtos.CategoryOutDto;
import ru.practicum.ewm.entities.Category;
import ru.practicum.ewm.mappers.CategoryMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryServiceTest {

    private final EntityManager em;
    private final CategoryService categoryService;
    private final CategoryAdminService categoryAdminService;

    private static CategoryInDto first;
    private static CategoryInDto second;
    private static CategoryInDto third;

    @BeforeAll
    public static void setUp() {
        first = CategoryInDto.builder()
                .name("Выставка собак")
                .build();

        second = CategoryInDto.builder()
                .name("Театр")
                .build();

        third = CategoryInDto.builder()
                .name("Музей")
                .build();
    }

    @Test
    void getCategories() {
        CategoryOutDto returnedOne = categoryAdminService.createCategory(first);
        CategoryOutDto returnedTwo = categoryAdminService.createCategory(second);
        CategoryOutDto returnedThree = categoryAdminService.createCategory(third);

        List<CategoryOutDto> categories = categoryService.getCategories(0, 10);

        assertThat(categories.size(), equalTo(3));
        assertThat(categories.get(0).getName(), equalTo(returnedOne.getName()));
        assertThat(categories.get(0).getId(), equalTo(returnedOne.getId()));
        assertThat(categories.get(1).getName(), equalTo(returnedTwo.getName()));
        assertThat(categories.get(1).getId(), equalTo(returnedTwo.getId()));
        assertThat(categories.get(2).getName(), equalTo(returnedThree.getName()));
        assertThat(categories.get(2).getId(), equalTo(returnedThree.getId()));
    }

    @Test
    void getCategoryById() {
        CategoryOutDto saved = categoryAdminService.createCategory(first);

        CategoryOutDto found = categoryService.getCategoryById(saved.getId());

        TypedQuery<Category> query = em.createQuery("Select c from Category c where c.id = :id",
                Category.class);
        Category category = query
                .setParameter("id", saved.getId())
                .getSingleResult();

        assertThat(found, notNullValue());
        assertThat(found, equalTo(CategoryMapper.toCategoryOut(category)));
        assertThat(found, equalTo(saved));
    }
}