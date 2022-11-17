package ru.practicum.ewm.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.utils.validator.IsLaterFromTheCurrentTime;
import ru.practicum.ewm.utils.validator.NullOrNotBlank;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventChangedDto {
    private Long id;
    @NullOrNotBlank(message = "Аннотация не должна быть пустой")
    @Size(min = 1, max = 512, message = "Длина аннотации должна быть от 1 до 512 символов")
    private String annotation;
    @NullOrNotBlank(message = "Описание не должно быть пустым")
    @Size(min = 1, max = 1000, message = "Описание должно содержать от 1 до 1000 символов")
    private String description;
    private Long category;
    @NullOrNotBlank(message = "Заголовок не должен быть пустым")
    @Size(min = 1, max = 512, message = "Заголовок должен содержать от 1 до 512 символов")
    private String title;
    private Integer participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @IsLaterFromTheCurrentTime(isNullable = true, message = "Событие не должно состояться ранее, " +
            "чем через два часа после текущего времени")
    private LocalDateTime eventDate;
    private Boolean paid;
}
