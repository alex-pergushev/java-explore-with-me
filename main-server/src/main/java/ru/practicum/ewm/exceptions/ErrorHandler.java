package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({DuplicateObjectException.class, ConditionIsNotMetException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(final RuntimeException e) {
        String reason = e.getClass() == ConditionIsNotMetException.class ?
                "Не соблюдены условия для запрошенной операции." : "Невозможно заменить объект ";
        return new ErrorResponse(e.getMessage(), reason, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ObjectNotFoundException.class, HttpMediaTypeNotAcceptableException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        String reason = e instanceof ObjectNotFoundException ? "Объект не был найден."
                : "HttpMediaType не принимается";
        return new ErrorResponse(e.getMessage(), reason, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoAccessRightsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleNoAccessRightsException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage(), "Недостаточные права доступа", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({InvalidRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequestException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage(), "Запрос был сделан с ошибками", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ErrorResponse(Objects.requireNonNull(e.getFieldError()).getDefaultMessage(),
                "Ошибка поля в объекте", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().findFirst().isPresent() ?
                e.getConstraintViolations().stream().findFirst().get().getMessage() : e.getMessage();
        return new ErrorResponse(message, "Ошибка в параметрах URI", HttpStatus.BAD_REQUEST);
    }
}
