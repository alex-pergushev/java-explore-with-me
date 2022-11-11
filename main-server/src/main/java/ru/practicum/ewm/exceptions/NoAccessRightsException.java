package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoAccessRightsException extends RuntimeException {
    public NoAccessRightsException(String message) {
        super(message);
        log.error(message);
    }
}
