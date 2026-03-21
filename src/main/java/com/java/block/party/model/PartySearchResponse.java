package com.java.block.party.model;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PartySearchResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Results results;
    private List<Index> items;
    private SortOrder sortOrder;
    private Item item;

}
