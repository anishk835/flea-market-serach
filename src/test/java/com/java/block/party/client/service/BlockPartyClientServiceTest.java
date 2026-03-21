package com.java.block.party.client.service;

import com.java.block.party.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockPartyClientServiceTest {

    @InjectMocks
    private BlockPartyClientService blockPartyClientService;

    @Mock
    private FleaMarketClientService fleaMarketClientService;

    private QueryParams queryParams;

    @BeforeEach
    void setUp() {
        queryParams = new QueryParams();
        queryParams.setSearchKeyWord("Flohmarkt");
    }

    @Test
    void testGetBlockParties() {
        AllItems expectedItems = new AllItems();
        Index index = new Index();
        expectedItems.setIndex(Arrays.asList(index));

        when(fleaMarketClientService.getBlockParties(any(QueryParams.class)))
                .thenReturn(Mono.just(expectedItems));

        Mono<AllItems> result = blockPartyClientService.getBlockParties(queryParams);

        StepVerifier.create(result)
                .expectNext(expectedItems)
                .verifyComplete();

        verify(fleaMarketClientService, times(1)).getBlockParties(queryParams);
    }

    @Test
    void testGetBlockPartyDetails() {
        ItemDetails expectedDetails = new ItemDetails();
        Item item = new Item();
        expectedDetails.setItem(item);

        when(fleaMarketClientService.getBlockPartyDetails(eq("123")))
                .thenReturn(Mono.just(expectedDetails));

        Mono<ItemDetails> result = blockPartyClientService.getBlockPartyDetails("123");

        StepVerifier.create(result)
                .expectNext(expectedDetails)
                .verifyComplete();

        verify(fleaMarketClientService, times(1)).getBlockPartyDetails("123");
    }

    @Test
    void testGetFleaMarketItemsGeoLocation() {
        var expectedGeoItems = new com.java.block.party.model.geo.ItemsGeoLocation();

        when(fleaMarketClientService.getFleaMarketItemsGeoLocation(any(QueryParams.class)))
                .thenReturn(Mono.just(expectedGeoItems));

        var result = blockPartyClientService.getFleaMarketItemsGeoLocation(queryParams);

        StepVerifier.create(result)
                .expectNext(expectedGeoItems)
                .verifyComplete();

        verify(fleaMarketClientService, times(1)).getFleaMarketItemsGeoLocation(queryParams);
    }
}
