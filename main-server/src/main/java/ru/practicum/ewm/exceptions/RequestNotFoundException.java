package ru.practicum.ewm.exceptions;

public class RequestNotFoundException extends ObjectNotFoundException {
    public RequestNotFoundException(String message) {
        super(message);
    }
}
