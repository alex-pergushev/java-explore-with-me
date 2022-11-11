package ru.practicum.ewm.exceptions;

public class UserNotFoundException extends ObjectNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
