package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DuplicateObjectException extends RuntimeException {
    public DuplicateObjectException(String message) {
        super(message);
        log.error(message);
    }
}
