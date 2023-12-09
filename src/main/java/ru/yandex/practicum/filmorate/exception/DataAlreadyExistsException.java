package ru.yandex.practicum.filmorate.exception;

public class DataAlreadyExistsException extends RuntimeException {

    public DataAlreadyExistsException(String message) {
        super(message);
    }
}
