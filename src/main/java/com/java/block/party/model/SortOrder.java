package com.java.block.party.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@EqualsAndHashCode
public class SortOrder {

    @Pattern(regexp = "^(bezirk|plz|tage)?$", message = "Sort type must be one of: bezirk, plz, tage")
    private String type = "";

    @NotNull(message = "Sort order cannot be null")
    private Order order;

}
