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
public class CompilationInDto {
    private long[] events;
    private boolean pinned;
    @NotBlank(message = "Заголовок не должен быть пустым")
    @Size(min = 1, max = 512, message = "Заголовок должен быть от 1 до 512 символов")
    private String title;
}
