package ru.practicum.ewm.exceptions;

public class CommentNotFoundException extends ObjectNotFoundException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
