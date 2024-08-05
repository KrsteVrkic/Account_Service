package account.exceptions;

import account.entities.responses.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            DataIntegrityViolationException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception e, HttpServletRequest request) {
        String message = getMessageFromException(e);
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler({
            UserExistException.class,
            PasswordTooShortException.class,
            PasswordBreachedException.class,
            PasswordEqualsException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomExceptions(Exception e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e, HttpServletRequest request) {
        return buildErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    private String getMessageFromException(Exception e) {
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            return ex.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        } else if (e instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) e;
            return ex.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
        } else if (e instanceof HttpMessageNotReadableException) {
            return "Malformed JSON request";
        } else {
            return e.getMessage();
        }
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, String path) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
