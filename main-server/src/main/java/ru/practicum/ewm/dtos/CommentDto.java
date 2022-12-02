package ru.practicum.ewm.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.enums.State;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private long id;
    @NotBlank(message = "Комментарий не может быть пустым.")
    @Size(max = 2000, message = "Максимальный размер комментария 2000 символов!")
    @Size(min = 2, message = "Минимальный размер комментария 2 символа!")
    private String text;
    private long event;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private State state = State.PENDING;

}
