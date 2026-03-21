package com.java.block.party.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SortOrderTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testValidSortOrderBezirk() {
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("bezirk");
        sortOrder.setOrder(Order.ASC);

        Set<ConstraintViolation<SortOrder>> violations = validator.validate(sortOrder);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidSortOrderPlz() {
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("plz");
        sortOrder.setOrder(Order.DESC);

        Set<ConstraintViolation<SortOrder>> violations = validator.validate(sortOrder);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidSortOrderTage() {
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("tage");
        sortOrder.setOrder(Order.ASC);

        Set<ConstraintViolation<SortOrder>> violations = validator.validate(sortOrder);
        assertThat(violations).isEmpty();
    }

    @Test
    void testEmptyTypeShouldBeValid() {
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("");
        sortOrder.setOrder(Order.ASC);

        Set<ConstraintViolation<SortOrder>> violations = validator.validate(sortOrder);
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidType() {
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("invalid");
        sortOrder.setOrder(Order.ASC);

        Set<ConstraintViolation<SortOrder>> violations = validator.validate(sortOrder);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("must be one of: bezirk, plz, tage");
    }

    @Test
    void testNullOrder() {
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("bezirk");
        sortOrder.setOrder(null);

        Set<ConstraintViolation<SortOrder>> violations = validator.validate(sortOrder);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("cannot be null");
    }

    @Test
    void testDefaultValues() {
        SortOrder sortOrder = new SortOrder();

        assertThat(sortOrder.getType()).isEqualTo("");
        assertThat(sortOrder.getOrder()).isNull();
    }

    @Test
    void testEqualsAndHashCode() {
        SortOrder sortOrder1 = new SortOrder();
        sortOrder1.setType("bezirk");
        sortOrder1.setOrder(Order.ASC);

        SortOrder sortOrder2 = new SortOrder();
        sortOrder2.setType("bezirk");
        sortOrder2.setOrder(Order.ASC);

        assertThat(sortOrder1).isEqualTo(sortOrder2);
        assertThat(sortOrder1.hashCode()).isEqualTo(sortOrder2.hashCode());
    }
}
