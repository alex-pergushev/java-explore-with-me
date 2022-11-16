package ru.practicum.ewm.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dtos.CategoryChangedDto;
import ru.practicum.ewm.dtos.CategoryInDto;
import ru.practicum.ewm.dtos.CategoryOutDto;
import ru.practicum.ewm.entities.Category;
import ru.practicum.ewm.mappers.CategoryMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryAdminServiceTest {

    private final EntityManager em;
    private final CategoryAdminService categoryAdminService;
    private static CategoryInDto categoryIn;

    @BeforeAll
    public static void setUp() {
        categoryIn = CategoryInDto.builder()
                .name("Выставка собак")
                .build();
    }

    @Test
    void createCategory() {
        categoryAdminService.createCategory(categoryIn);

        TypedQuery<Category> query = em.createQuery("Select c from Category c where c.name = :name",
                Category.class);
        Category category = query
                .setParameter("name", categoryIn.getName())
                .getSingleResult();

        assertThat(category, notNullValue());
        assertThat(category.getId(), notNullValue());
    }

    @Test
    void updateCategory() {
        CategoryOutDto old = categoryAdminService.createCategory(categoryIn);
        CategoryChangedDto categoryChanged = CategoryChangedDto.builder()
                .id(old.getId())
                .name("Выставка ездовых собак")
                .build();

        CategoryOutDto updated = categoryAdminService.updateCategory(categoryChanged);

        TypedQuery<Category> query = em.createQuery("Select c from Category c where c.id = :savedId",
                Category.class);
        Category category = query
                .setParameter("savedId", old.getId())
                .getSingleResult();

        assertThat(category, notNullValue());
        assertThat(updated, equalTo(CategoryMapper.toCategoryOut(category)));
        assertThat(updated.getName(), equalTo("Выставка ездовых собак"));
        assertThat(updated.getId(), equalTo(old.getId()));
    }

    @Test
    void deleteCategory() {
        CategoryOutDto old = categoryAdminService.createCategory(categoryIn);
        categoryAdminService.deleteCategory(old.getId());
        Category category = em.find(Category.class, old.getId());
        Assertions.assertNull(category);
    }
}