package ru.practicum.ewm.dtos;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public abstract class EventOutDto {
    private Long id;
    private String annotation;
    private CategoryOutDto category;
    private UserShortOutDto initiator;
    private String title;
    private int confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private boolean paid;
    private long views;
}
