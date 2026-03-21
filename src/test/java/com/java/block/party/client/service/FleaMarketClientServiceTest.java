package com.java.block.party.client.service;

import com.java.block.party.config.UrlBuilder;
import com.java.block.party.model.*;
import com.java.block.party.model.geo.ItemsGeoLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FleaMarketClientServiceTest {

    @InjectMocks
    private FleaMarketClientService fleaMarketClientService;

    @Mock
    private WebClient webClient;

    @Mock
    private UrlBuilder urlBuilder;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private QueryParams queryParams;

    @BeforeEach
    void setUp() {
        queryParams = new QueryParams();
        queryParams.setSearchKeyWord("Flohmarkt");
        queryParams.setDistrict("Berlin");
        queryParams.setResultPerPage(10);

        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("bezirk");
        sortOrder.setOrder(Order.ASC);
        queryParams.setSortOrder(sortOrder);
    }

    @Test
    void testGetBlockParties() {
        AllItems expectedItems = new AllItems();
        when(urlBuilder.getAllItemsJsonUrl()).thenReturn("http://example.com/items");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AllItems.class)).thenReturn(Mono.just(expectedItems));

        Mono<AllItems> result = fleaMarketClientService.getBlockParties(queryParams);

        StepVerifier.create(result)
                .expectNext(expectedItems)
                .verifyComplete();

        verify(webClient, times(1)).get();
    }

    @Test
    void testGetBlockPartiesWithEmptyParams() {
        QueryParams emptyParams = new QueryParams();
        AllItems expectedItems = new AllItems();

        when(urlBuilder.getAllItemsJsonUrl()).thenReturn("http://example.com/items");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AllItems.class)).thenReturn(Mono.just(expectedItems));

        Mono<AllItems> result = fleaMarketClientService.getBlockParties(emptyParams);

        StepVerifier.create(result)
                .expectNext(expectedItems)
                .verifyComplete();
    }

    @Test
    void testGetBlockPartyDetails() {
        ItemDetails expectedDetails = new ItemDetails();
        when(urlBuilder.getItemJsonUrl()).thenReturn("http://example.com/item/{id}");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ItemDetails.class)).thenReturn(Mono.just(expectedDetails));

        Mono<ItemDetails> result = fleaMarketClientService.getBlockPartyDetails("123");

        StepVerifier.create(result)
                .expectNext(expectedDetails)
                .verifyComplete();

        verify(webClient, times(1)).get();
    }

    @Test
    void testGetFleaMarketItemsGeoLocation() {
        ItemsGeoLocation expectedGeoItems = new ItemsGeoLocation();
        when(urlBuilder.getItemsGeoJsonUrl()).thenReturn("http://example.com/items.geojson");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ItemsGeoLocation.class)).thenReturn(Mono.just(expectedGeoItems));

        Mono<ItemsGeoLocation> result = fleaMarketClientService.getFleaMarketItemsGeoLocation(queryParams);

        StepVerifier.create(result)
                .expectNext(expectedGeoItems)
                .verifyComplete();

        verify(webClient, times(1)).get();
    }

    @Test
    void testGetFleaMarketItemsGeoLocationWithNullParams() {
        ItemsGeoLocation expectedGeoItems = new ItemsGeoLocation();
        when(urlBuilder.getItemsGeoJsonUrl()).thenReturn("http://example.com/items.geojson");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ItemsGeoLocation.class)).thenReturn(Mono.just(expectedGeoItems));

        Mono<ItemsGeoLocation> result = fleaMarketClientService.getFleaMarketItemsGeoLocation(null);

        StepVerifier.create(result)
                .expectNext(expectedGeoItems)
                .verifyComplete();
    }

    @Test
    void testGetBlockPartiesError() {
        when(urlBuilder.getAllItemsJsonUrl()).thenReturn("http://example.com/items");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AllItems.class))
                .thenReturn(Mono.error(new RuntimeException("Connection error")));

        Mono<AllItems> result = fleaMarketClientService.getBlockParties(queryParams);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
