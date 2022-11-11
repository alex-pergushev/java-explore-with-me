package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConditionIsNotMetException extends RuntimeException {
    public ConditionIsNotMetException(String message) {
        super(message);
        log.error(message);
    }
}
