package com.java.block.party.template.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorTest {

    @Test
    void testApiErrorWithStatus() {
        ApiError apiError = new ApiError(org.springframework.http.HttpStatus.BAD_REQUEST);

        assertThat(apiError.getStatus()).isEqualTo(org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    @Test
    void testApiErrorWithStatusAndMessage() {
        ApiError apiError = new ApiError(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Resource not found",
                new RuntimeException("Test exception"));

        assertThat(apiError.getStatus()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(apiError.getMessage()).isEqualTo("Resource not found");
        assertThat(apiError.getDebugMessage()).isEqualTo("Test exception");
    }

    @Test
    void testApiErrorSetters() {
        ApiError apiError = new ApiError(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage("Custom message");
        apiError.setDebugMessage("Custom debug message");

        assertThat(apiError.getMessage()).isEqualTo("Custom message");
        assertThat(apiError.getDebugMessage()).isEqualTo("Custom debug message");
    }
}
