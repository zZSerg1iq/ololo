package ru.zinoviev.questbot.message_handlerservice.exception;

public class EntityNotFound extends RuntimeException {
    public EntityNotFound(String message) {
        super(message);
    }
}
