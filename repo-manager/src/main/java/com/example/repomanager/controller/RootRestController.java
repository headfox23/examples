package com.example.repomanager.controller;

import com.example.repomanager.model.general.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;


@Slf4j
@RestController
@RequestMapping(RootRestController.ROOT_URL)
@Validated // needed for validation annotations on plain request params
public class RootRestController {
    private static final String NAME = "api";
    public static final String ROOT_URL = "/" + NAME;
    public static final String EXCEPTION_TEXT_NOT_AUTHORIZED = "You are not authorized to access this resource";

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,})
    public ResponseEntity<ErrorMessage> badArguments(Exception ex) {
        log.info(ex.getMessage(), ex);
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                Instant.now(),
                ex.getMessage(),
                "Please validate if all parameters are correctly set.");

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpClientErrorException.class})
    public ResponseEntity<ErrorMessage> clientErrors(HttpClientErrorException ex) {
        log.info(ex.getMessage(), ex);
        ErrorMessage message = new ErrorMessage(
                ex.getStatusCode().value(),
                Instant.now(),
                ex.getMessage(),
                ex.getStatusText());

        return new ResponseEntity<>(message, ex.getStatusCode());
    }

    @ExceptionHandler(value = {OptimisticLockingFailureException.class})
    public ResponseEntity<ErrorMessage> optimisticLockException(OptimisticLockingFailureException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                Instant.now(),
                ex.getMessage(),
                "Parallel changes on the same object. Change discarded.");

        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {ClientAbortException.class})
    public void clientAbortException(ClientAbortException ex) {
        log.info(ex.getMessage());
        // this is usually just if the client cancels a request...
    }

    private static String getEndpoint(HttpServletRequest request) {
        String endpoint = "unknown";
        if (request != null) {
            endpoint = request.getRequestURI();
        }
        return endpoint;
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ErrorMessage> unauthorizedException(AccessDeniedException ex) {
        log.warn(ex.getMessage(), ex);
        ErrorMessage message = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                Instant.now(),
                EXCEPTION_TEXT_NOT_AUTHORIZED,
                "Either you are not allowed to access this resource or you did not add the token header correctly.");
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorMessage> allOtherExceptions(Exception ex, HttpServletRequest request) {
        String endpoint = getEndpoint(request);
        log.error("[{}] {}", endpoint, ex.getMessage(), ex);
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now(),
                ex.getMessage(),
                "Something unexpected happened.");

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
