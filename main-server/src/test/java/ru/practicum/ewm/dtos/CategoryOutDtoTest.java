package ru.practicum.ewm.dtos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CategoryOutDtoTest {
    @Autowired
    private JacksonTester<CategoryOutDto> json;

    @Test
    void testCategoryOutDto() throws IOException {
        CategoryOutDto category = CategoryOutDto.builder()
                .id(5)
                .name("Театр")
                .build();

        JsonContent<CategoryOutDto> result = json.write(category);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Театр");
    }
}