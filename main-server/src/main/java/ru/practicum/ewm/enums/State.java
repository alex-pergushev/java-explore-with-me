package ru.practicum.ewm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum State {
    REJECTED("Отклонен"),
    PENDING("На рассмотрении"),
    PUBLISHED("Опубликован"),
    CANCELED("Отменен");

    private final String status;
}
