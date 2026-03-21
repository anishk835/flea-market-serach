package com.java.block.party.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PartySearchResponseTest {

    @Test
    void testGettersAndSetters() {
        PartySearchResponse response = new PartySearchResponse();

        Results results = new Results();
        List<Index> items = Arrays.asList(new Index(), new Index());
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("bezirk");
        sortOrder.setOrder(Order.ASC);
        Item item = new Item();

        response.setResults(results);
        response.setItems(items);
        response.setSortOrder(sortOrder);
        response.setItem(item);

        assertThat(response.getResults()).isEqualTo(results);
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getSortOrder()).isEqualTo(sortOrder);
        assertThat(response.getItem()).isEqualTo(item);
    }

    @Test
    void testToString() {
        PartySearchResponse response = new PartySearchResponse();
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("tage");
        response.setSortOrder(sortOrder);

        String toString = response.toString();
        assertThat(toString).isNotNull();
        assertThat(toString).contains("PartySearchResponse");
    }
}
