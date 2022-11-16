package ru.practicum.ewm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    PENDING("На рассмотрении"),
    CONFIRMED("Подтвержден"),
    REJECTED("Отклонен"),
    CANCELED("Отменен");

    private final String status;
}
