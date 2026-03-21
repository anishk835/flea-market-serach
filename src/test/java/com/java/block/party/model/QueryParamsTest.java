package com.java.block.party.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParamsTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testValidQueryParams() {
        QueryParams params = new QueryParams();
        params.setSearchKeyWord("Flohmarkt");
        params.setDistrict("Mitte");
        params.setResultPerPage(20);

        Set<ConstraintViolation<QueryParams>> violations = validator.validate(params);
        assertThat(violations).isEmpty();
    }

    @Test
    void testSearchKeyWordTooLong() {
        QueryParams params = new QueryParams();
        params.setSearchKeyWord("a".repeat(101)); // 101 characters

        Set<ConstraintViolation<QueryParams>> violations = validator.validate(params);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("must not exceed 100 characters");
    }

    @Test
    void testSearchKeyWordWithInvalidCharacters() {
        QueryParams params = new QueryParams();
        params.setSearchKeyWord("test<script>alert('xss')</script>");

        Set<ConstraintViolation<QueryParams>> violations = validator.validate(params);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("invalid characters");
    }

    @Test
    void testSearchKeyWordWithGermanCharacters() {
        QueryParams params = new QueryParams();
        params.setSearchKeyWord("Flöhmarkt Möbel äöüÄÖÜß");

        Set<ConstraintViolation<QueryParams>> violations = validator.validate(params);
        assertThat(violations).isEmpty();
    }

    @Test
    void testDistrictTooLong() {
        QueryParams params = new QueryParams();
        params.setDistrict("a".repeat(51)); // 51 characters

        Set<ConstraintViolation<QueryParams>> violations = validator.validate(params);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("must not exceed 50 characters");
    }

    @Test
    void testDistrictWithInvalidCharacters() {
        QueryParams params = new QueryParams();
        params.setDistrict("Berlin123");

        Set<ConstraintViolation<QueryParams>> violations = validator.validate(params);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("invalid characters");
    }

    @Test
    void testResultPerPageTooSmall() {
        QueryParams params = new QueryParams();
        params.setResultPerPage(0);

        Set<ConstraintViolation<QueryParams>> violations = validator.validate(params);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("must be at least 1");
    }

    @Test
    void testResultPerPageTooLarge() {
        QueryParams params = new QueryParams();
        params.setResultPerPage(101);

        Set<ConstraintViolation<QueryParams>> violations = validator.validate(params);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("must not exceed 100");
    }

    @Test
    void testDefaultValues() {
        QueryParams params = new QueryParams();

        assertThat(params.getSearchKeyWord()).isEqualTo("");
        assertThat(params.getDistrict()).isEqualTo("");
        assertThat(params.getResultPerPage()).isEqualTo(10);
        assertThat(params.getSortOrder()).isNull();
    }

    @Test
    void testEqualsAndHashCode() {
        QueryParams params1 = new QueryParams();
        params1.setSearchKeyWord("test");
        params1.setDistrict("Berlin");
        params1.setResultPerPage(20);

        QueryParams params2 = new QueryParams();
        params2.setSearchKeyWord("test");
        params2.setDistrict("Berlin");
        params2.setResultPerPage(20);

        assertThat(params1).isEqualTo(params2);
        assertThat(params1.hashCode()).isEqualTo(params2.hashCode());
    }

    @Test
    void testToString() {
        QueryParams params = new QueryParams();
        params.setSearchKeyWord("test");
        params.setDistrict("Berlin");

        String toString = params.toString();
        assertThat(toString).contains("test");
        assertThat(toString).contains("Berlin");
    }
}
