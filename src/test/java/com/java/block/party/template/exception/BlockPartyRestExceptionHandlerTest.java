package com.java.block.party.template.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BlockPartyRestExceptionHandlerTest {

    private BlockPartyRestExceptionHandler exceptionHandler;
    private ServletWebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new BlockPartyRestExceptionHandler();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        webRequest = new ServletWebRequest(servletRequest);
    }

    @Test
    void testHandleMissingServletRequestParameter() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("id", "String");

        ResponseEntity<Object> response = exceptionHandler.handleMissingServletRequestParameter(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError apiError = (ApiError) response.getBody();
        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).contains("id parameter is missing");
    }

    @Test
    void testHandleHttpMediaTypeNotSupported() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(
                MediaType.TEXT_PLAIN,
                Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));

        ResponseEntity<Object> response = exceptionHandler.handleHttpMediaTypeNotSupported(
                ex, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        ApiError apiError = (ApiError) response.getBody();
        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).contains("media type is not supported");
        assertThat(apiError.getMessage()).contains("application/json");
    }

    @Test
    void testHandleHttpMessageNotReadable() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<Object> response = exceptionHandler.handleHttpMessageNotReadable(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError apiError = (ApiError) response.getBody();
        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).isEqualTo("Malformed JSON request");
    }

    @Test
    void testHandleNoHandlerFoundException() {
        NoHandlerFoundException ex = new NoHandlerFoundException(
                "GET", "/nonexistent", new HttpHeaders());

        ResponseEntity<Object> response = exceptionHandler.handleNoHandlerFoundException(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError apiError = (ApiError) response.getBody();
        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).contains("Could not find the GET method");
        assertThat(apiError.getMessage()).contains("/nonexistent");
    }

    @Test
    void testHandleMethodArgumentNotValid() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "searchKeyWord", "must not be empty");
        FieldError fieldError2 = new FieldError("object", "resultPerPage", "must be at least 1");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError apiError = (ApiError) response.getBody();
        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).contains("Validation failed");
        assertThat(apiError.getMessage()).contains("searchKeyWord");
        assertThat(apiError.getMessage()).contains("resultPerPage");
    }

    @Test
    void testHandleMethodArgumentTypeMismatch() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Integer.class, "id", null, null);

        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentTypeMismatch(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError apiError = (ApiError) response.getBody();
        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).contains("parameter 'id'");
        assertThat(apiError.getMessage()).contains("'abc'");
        assertThat(apiError.getMessage()).contains("Integer");
    }

    @Test
    void testHandleConstraintViolation() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("searchKeyWord");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("must not be empty");

        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        Path path2 = mock(Path.class);
        when(path2.toString()).thenReturn("resultPerPage");
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("must be at least 1");

        violations.add(violation1);
        violations.add(violation2);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<Object> response = exceptionHandler.handleConstraintViolation(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError apiError = (ApiError) response.getBody();
        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).contains("Validation failed");
    }

    @Test
    void testHandleWebClientResponseException() {
        WebClientResponseException ex = WebClientResponseException.create(
                404, "Not Found", new HttpHeaders(), new byte[0], null);

        ResponseEntity<Object> response = exceptionHandler.handleWebClientResponseException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiError apiError = (ApiError) response.getBody();
        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).isEqualTo("External service returned an error");
        assertThat(apiError.getDebugMessage()).isEqualTo("Failed to retrieve data from external API");
    }

    @Test
    void testHandleWebClientRequestException() {
        WebClientRequestException ex = new WebClientRequestException(
                new RuntimeException("Connection refused"), null, URI.create("http://example.com"), null);

        ResponseEntity<Object> response = exceptionHandler.handleWebClientRequestException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        ApiError apiError = (ApiError) response.getBody();
        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).isEqualTo("Unable to connect to external service");
        assertThat(apiError.getDebugMessage()).isEqualTo("Network error occurred while connecting to external API");
    }
}
