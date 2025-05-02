package id.ac.ui.cs.advprog.orderservice.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleRuntimeException_ShouldReturnBadRequestWithErrorMessage() {
        String errorMessage = "Test error message";
        RuntimeException exception = new RuntimeException(errorMessage);

        ResponseEntity<Map<String, String>> responseEntity = exceptionHandler.handleRuntimeException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().containsKey("error"));
        assertEquals(errorMessage, responseEntity.getBody().get("error"));
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithFieldErrors() {
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("object", "field1", "Error message 1"));
        fieldErrors.add(new FieldError("object", "field2", "Error message 2"));

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));

        ResponseEntity<Map<String, String>> responseEntity = exceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().containsKey("field1"));
        assertTrue(responseEntity.getBody().containsKey("field2"));
        assertEquals("Error message 1", responseEntity.getBody().get("field1"));
        assertEquals("Error message 2", responseEntity.getBody().get("field2"));
    }
}