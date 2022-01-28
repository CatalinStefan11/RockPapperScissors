package com.rockpaperscissors.exception;

import com.rockpaperscissors.exception.customexceptions.AlreadyExistsException;
import com.rockpaperscissors.exception.customexceptions.InvalidOperationException;
import com.rockpaperscissors.exception.customexceptions.NotFoundException;
import com.rockpaperscissors.exception.model.ClientError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static com.rockpaperscissors.utils.AppConstants.GENERIC_ERROR_MESSAGE;
import static com.rockpaperscissors.utils.AppConstants.NOT_FOUND_ERROR_MESSAGE;

@RestControllerAdvice
@Slf4j
public class AppExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ClientError> exception(Exception exception) {

        log.warn("An exception has occurred: {}", exception.getMessage());
        return ResponseEntity.internalServerError().body(
                new ClientError(LocalDateTime.now(),
                        GENERIC_ERROR_MESSAGE,
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));

    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ClientError> exception(NotFoundException exception) {

        log.warn("An exception has occurred: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ClientError(LocalDateTime.now(),
                        exception.getMessage(),
                        HttpStatus.NOT_FOUND.value()));


    }

    @ExceptionHandler(value = InvalidOperationException.class)
    public ResponseEntity<ClientError> exception(InvalidOperationException exception) {

        log.warn("An exception has occurred: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ClientError(LocalDateTime.now(),
                        exception.getMessage(),
                        HttpStatus.NOT_FOUND.value()));


    }

    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity<ClientError> exception(AlreadyExistsException exception) {

        log.warn("An exception has occurred: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ClientError(LocalDateTime.now(),
                        exception.getMessage(),
                        HttpStatus.BAD_REQUEST.value()));


    }


}


