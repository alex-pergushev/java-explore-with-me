package ru.practicum.ewm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortingEvents {
    EVENT_DATE("Дата события"),
    VIEWS("Просмотры");

    private final String sort;
}