package com.ayilmaz.timetracker.timetrackerbff.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handleException(MissingServletRequestParameterException ex) {
        LOGGER.debug(ex.getMessage(), ex);

        return createResponse(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handleException(ConstraintViolationException ex) {
        LOGGER.debug(ex.getMessage(), ex);

        return createResponse(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handleException(MethodArgumentNotValidException ex) {
        LOGGER.debug(ex.getMessage(), ex);
        
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return createResponse(errors);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handleException(HttpClientErrorException.BadRequest ex) {

        LOGGER.error(ex.getMessage(), ex);

        // In a real-world application, a better logic can be implemented for generating error messages
        return createResponse("Bad Request!");
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map handleException(Exception ex) {

        LOGGER.error(ex.getMessage(), ex);

        return createResponse(Arrays.asList("Internal Server Error!"));
    }

    private Map createResponse(List<String> errors) {
        return Collections.singletonMap("errors", errors);
    }

    private Map createResponse(String message) {
        return createResponse(Arrays.asList(message));
    }

}
