package com.java.block.party.template.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BlockPartySearchExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String errorMessage = "Service unavailable";
        BlockPartySearchException exception = new BlockPartySearchException(errorMessage);

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void testExceptionIsThrown() {
        assertThatThrownBy(() -> {
            throw new BlockPartySearchException("Test exception");
        })
                .isInstanceOf(BlockPartySearchException.class)
                .hasMessage("Test exception");
    }

    @Test
    void testExceptionExtendsRuntimeException() {
        BlockPartySearchException exception = new BlockPartySearchException("Test");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
