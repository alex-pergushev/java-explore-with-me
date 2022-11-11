package ru.practicum.ewm.exceptions;

public class EventNotFoundException extends ObjectNotFoundException {
    public EventNotFoundException(String message)  {
        super(message);
    }
}
