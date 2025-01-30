package ru.zinoviev.questbot.message_handlerservice.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zinoviev.questbot.message_handlerservice.exception.EntityNotFound;

@RestControllerAdvice
public class DomainServicesExceptionAdvice {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(EntityNotFound.class)
    String entityNotFoundExceptionHandler(Exception e) {
        return "entity not found: " + e;
    }
}
