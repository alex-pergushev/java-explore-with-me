package ru.practicum.ewm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInDto {
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 1, max = 256, message = "Длина имени пользователя должна быть от 1 до 256 символов")
    private String name;
    @Email(message = "Не корректный aдрес электронной почты")
    private String email;
}