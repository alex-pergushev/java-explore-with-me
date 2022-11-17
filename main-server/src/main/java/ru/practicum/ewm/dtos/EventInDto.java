package ru.practicum.ewm.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.utils.validator.IsLaterFromTheCurrentTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventInDto {
    @NotBlank(message = "Аннотация не должна быть пустой")
    @Size(min = 1, max = 512, message = "Длина аннотации должна быть от 1 до 512 символов")
    private String annotation;
    private long category;
    private LocationDto location;
    @NotBlank(message = "Заголовок не должен быть пустым")
    @Size(min = 1, max = 512, message = "Заголовок должен содержать от 1 до 512 символов")
    private String title;
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(min = 1, max = 1000, message = "Описание должно содержать от 1 до 1000 символов")
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @IsLaterFromTheCurrentTime(message = "Событие не должно состояться ранее, чем через два часа после текущего времени")
    private LocalDateTime eventDate;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
}
