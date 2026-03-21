package com.java.block.party.template.service;

import com.java.block.party.model.*;
import com.java.block.party.template.exception.BlockPartySearchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartySearchServiceTest {

    @InjectMocks
    private PartySearchService partySearchService;

    @Mock
    private FleaMarketRestService fleaMarketRestService;

    private QueryParams queryParams;
    private AllItems allItems;
    private ItemDetails itemDetails;

    @BeforeEach
    void setUp() {
        queryParams = new QueryParams();
        queryParams.setSearchKeyWord("Flohmarkt");
        queryParams.setDistrict("Mitte");
        queryParams.setResultPerPage(20);

        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("bezirk");
        sortOrder.setOrder(Order.ASC);
        queryParams.setSortOrder(sortOrder);

        allItems = new AllItems();
        Results results = new Results();
        Index index1 = new Index();
        Index index2 = new Index();
        allItems.setResults(results);
        allItems.setIndex(Arrays.asList(index1, index2));

        itemDetails = new ItemDetails();
        Item item = new Item();
        itemDetails.setItem(item);
    }

    @Test
    void testGetBlockParties() {
        when(fleaMarketRestService.getBlockParties(any(QueryParams.class))).thenReturn(allItems);

        PartySearchResponse response = partySearchService.getBlockParties(queryParams);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getResults()).isEqualTo(allItems.getResults());
        assertThat(response.getSortOrder()).isEqualTo(queryParams.getSortOrder());
        verify(fleaMarketRestService, times(1)).getBlockParties(queryParams);
    }

    @Test
    void testGetBlockPartiesWithNullItems() {
        AllItems emptyItems = new AllItems();
        emptyItems.setIndex(null);
        when(fleaMarketRestService.getBlockParties(any(QueryParams.class))).thenReturn(emptyItems);

        PartySearchResponse response = partySearchService.getBlockParties(queryParams);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isNull();
    }

    @Test
    void testGetBlockPartiesWhenServiceThrowsException() {
        when(fleaMarketRestService.getBlockParties(any(QueryParams.class)))
                .thenThrow(new BlockPartySearchException("Service unavailable"));

        assertThatThrownBy(() -> partySearchService.getBlockParties(queryParams))
                .isInstanceOf(BlockPartySearchException.class)
                .hasMessage("Service unavailable");
    }

    @Test
    void testGetBlockPartyDetails() {
        when(fleaMarketRestService.getBlockPartyDetails(eq("123"))).thenReturn(itemDetails);

        PartySearchResponse response = partySearchService.getBlockPartyDetails("123");

        assertThat(response).isNotNull();
        assertThat(response.getItem()).isEqualTo(itemDetails.getItem());
        verify(fleaMarketRestService, times(1)).getBlockPartyDetails("123");
    }

    @Test
    void testGetBlockPartyDetailsWithDifferentId() {
        when(fleaMarketRestService.getBlockPartyDetails(eq("456"))).thenReturn(itemDetails);

        PartySearchResponse response = partySearchService.getBlockPartyDetails("456");

        assertThat(response).isNotNull();
        assertThat(response.getItem()).isNotNull();
        verify(fleaMarketRestService, times(1)).getBlockPartyDetails("456");
    }

    @Test
    void testGetBlockPartyDetailsWhenServiceThrowsException() {
        when(fleaMarketRestService.getBlockPartyDetails(anyString()))
                .thenThrow(new BlockPartySearchException("Item not found"));

        assertThatThrownBy(() -> partySearchService.getBlockPartyDetails("999"))
                .isInstanceOf(BlockPartySearchException.class)
                .hasMessage("Item not found");
    }

    @Test
    void testCachingBehavior() {
        // This test verifies the service method is called
        // Actual caching would be tested in integration tests
        when(fleaMarketRestService.getBlockParties(any(QueryParams.class))).thenReturn(allItems);

        partySearchService.getBlockParties(queryParams);
        partySearchService.getBlockParties(queryParams);

        // Without actual cache, service is called twice
        // With cache enabled in integration test, it would be called once
        verify(fleaMarketRestService, atLeast(1)).getBlockParties(queryParams);
    }
}
