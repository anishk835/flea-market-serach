package com.java.block.party.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class QueryParams {

    @Pattern(regexp = "^[a-zA-Z0-9\\säöüÄÖÜß-]*$", message = "Search keyword contains invalid characters")
    @Size(max = 100, message = "Search keyword must not exceed 100 characters")
    private String searchKeyWord = "";

    @Pattern(regexp = "^[a-zA-Z\\säöüÄÖÜß-]*$", message = "District contains invalid characters")
    @Size(max = 50, message = "District must not exceed 50 characters")
    private String district = "";

    @Min(value = 1, message = "Results per page must be at least 1")
    @Max(value = 100, message = "Results per page must not exceed 100")
    private int resultPerPage = 10;

    @Valid
    private SortOrder sortOrder;

}
