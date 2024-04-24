package org.ntnu.idi.idatt2106.sparesti.sparestibackend.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.exception.goal.ActiveGoalLimitExceededException;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.exception.goal.GoalNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Global exception handler to handle different types of exceptions across the application. It provides centralized
 * exception handling for various types of exceptions that may occur during the execution of the application.
 *
 * @author Harry L.X & Lars M.L.N
 * @version 1.0
 * @since 17.4.24
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Log the error to the logger.
     *
     * @param ex
     *            The exception to be logged.
     */
    private void logError(Exception ex) {
        ex.printStackTrace();
        logger.error("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
    }

    /**
     * Handle exceptions related to existing objects.
     *
     * @param ex
     *            The exception indicating that an object already exists.
     * @return ResponseEntity with an appropriate HTTP status code and error message.
     */
    @ExceptionHandler(
            value = {
                UserAlreadyExistsException.class,
                ChallengeConfigAlreadyExistsException.class,
                ChallengeTypeConfigAlreadyExistsException.class
            })
    public ResponseEntity<String> handleObjectAlreadyExistException(Exception ex) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Handle exceptions related to non-existing objects.
     *
     * @param ex
     *            The exception indicating that an object does not exist.
     * @return ResponseEntity with an appropriate HTTP status code and error message.
     */
    @ExceptionHandler(
            value = {
                UserNotFoundException.class,
                UsernameNotFoundException.class,
                GoalNotFoundException.class,
                ChallengeConfigNotFoundException.class,
                ChallengeNotFoundException.class,
                ChallengeTypeConfigNotFoundException.class,
                ConfigNotFoundException.class,
            })
    public ResponseEntity<String> handleObjectDoesNotExistException(Exception ex) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handle exceptions related to bad input or invalid requests.
     *
     * @param ex
     *            The exception indicating bad input or invalid request.
     * @return ResponseEntity with an appropriate HTTP status code and error message.
     */
    @ExceptionHandler(
            value = {
                IllegalArgumentException.class,
                HttpMessageNotReadableException.class,
                BadInputException.class,
                NullPointerException.class,
                MissingServletRequestParameterException.class,
                HttpRequestMethodNotSupportedException.class,
                DataIntegrityViolationException.class,
                MessagingException.class,
                MethodArgumentNotValidException.class,
                ActiveGoalLimitExceededException.class,
                ChallengeAlreadyCompletedException.class
            })
    public ResponseEntity<String> handleBadInputException(Exception ex) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handle exceptions related to constraint violations in validation.
     *
     * @param ex
     *            The exception indicating constraint violations during validation.
     * @return ResponseEntity with an appropriate HTTP status code and error message.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<String> handleConstraintViolationException(
            ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }

        String errorMessage = String.join(", ", errors);
        return ResponseEntity.badRequest().body(errorMessage);
    }
    /*
    /**
     * Handle any remaining exceptions that are not explicitly handled.
     *
     * @param ex
     *            The exception that is not explicitly handled.
     * @return ResponseEntity with an appropriate HTTP status code and error message.

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<String> handleRemainderExceptions(Exception ex) {
      logError(ex);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getClass().getSimpleName());
    }
    */
}
