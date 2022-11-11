package ru.practicum.ewm.exceptions;

public class CategoryNotFoundException extends ObjectNotFoundException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
