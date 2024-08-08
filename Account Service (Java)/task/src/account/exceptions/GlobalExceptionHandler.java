package account.exceptions;

import account.entities.responses.ErrorResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
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

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            DataIntegrityViolationException.class,
            IllegalArgumentException.class,
            EntityExistsException.class,
            UserExistException.class,
            PasswordTooShortException.class,
            PasswordBreachedException.class,
            PasswordEqualsException.class,
            ParseException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleExceptions(Exception e, HttpServletRequest request) {
        HttpStatus status;
        if (e instanceof EntityNotFoundException) status = HttpStatus.NOT_FOUND;
        else status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(MethodArgumentNotValidException e,
                                                               HttpServletRequest request) {
        List<String> messages = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        String message = String.join(", ", messages);
        ErrorResponse body = new ErrorResponse(
                400,
                "Bad Request",
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
