package com.assignment.filehandlingapp.api;


import com.assignment.filehandlingapp.domain.CustomErrorMessage;
import com.assignment.filehandlingapp.exception.FileNotFoundException;
import com.assignment.filehandlingapp.exception.FileStorageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FileHandlingExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomErrorMessage handleException(IllegalArgumentException e) {
        return new CustomErrorMessage(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomErrorMessage handleException(Exception e) {
        return new CustomErrorMessage(e.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomErrorMessage handleException(FileNotFoundException e) {
        return new CustomErrorMessage(e.getMessage());
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomErrorMessage handleException(FileStorageException e) {
        return new CustomErrorMessage(e.getMessage());
    }

}
