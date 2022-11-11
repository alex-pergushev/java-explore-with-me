package ru.practicum.ewm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryChangedDto {
    private long id;
    @NotBlank(message = "Название категории не должно быть пустым")
    @Size(min = 1, max = 64, message = "Длина названия категории должна быть от 1 до 64 символов")
    private String name;
}
