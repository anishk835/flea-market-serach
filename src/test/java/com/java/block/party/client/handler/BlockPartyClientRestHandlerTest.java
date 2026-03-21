package com.java.block.party.client.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.block.party.client.service.BlockPartyClientService;
import com.java.block.party.model.*;
import com.java.block.party.model.geo.ItemsGeoLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class BlockPartyClientRestHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlockPartyClientService blockPartyClientService;

    private AllItems allItems;
    private ItemDetails itemDetails;
    private ItemsGeoLocation geoLocation;
    private QueryParams queryParams;

    @BeforeEach
    void setUp() {
        allItems = new AllItems();
        Index index = new Index();
        allItems.setIndex(Arrays.asList(index));

        itemDetails = new ItemDetails();
        Item item = new Item();
        itemDetails.setItem(item);

        geoLocation = new ItemsGeoLocation();

        queryParams = new QueryParams();
        queryParams.setSearchKeyWord("Flohmarkt");
        queryParams.setDistrict("Berlin");
        queryParams.setResultPerPage(10);
    }

    @Test
    void testGetBlockPartyDetailsSuccess() {
        when(blockPartyClientService.getBlockPartyDetails(eq("123")))
                .thenReturn(Mono.just(itemDetails));

        webTestClient.get()
                .uri("/client/party/query?id=123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDetails.class);

        verify(blockPartyClientService, times(1)).getBlockPartyDetails("123");
    }

    @Test
    void testGetBlockPartyDetailsWithInvalidId() {
        webTestClient.get()
                .uri("/client/party/query?id=abc")
                .exchange()
                .expectStatus().isBadRequest();

        verify(blockPartyClientService, never()).getBlockPartyDetails(anyString());
    }

    @Test
    void testGetBlockPartiesSuccess() {
        when(blockPartyClientService.getBlockParties(any(QueryParams.class)))
                .thenReturn(Mono.just(allItems));

        webTestClient.post()
                .uri("/client/parties")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(queryParams)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AllItems.class);

        verify(blockPartyClientService, times(1)).getBlockParties(any(QueryParams.class));
    }

    @Test
    void testGetBlockPartiesWithInvalidSearchKeyword() {
        queryParams.setSearchKeyWord("<script>alert('xss')</script>");

        webTestClient.post()
                .uri("/client/parties")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(queryParams)
                .exchange()
                .expectStatus().isBadRequest();

        verify(blockPartyClientService, never()).getBlockParties(any(QueryParams.class));
    }

    @Test
    void testGetBlockPartiesWithInvalidResultPerPage() {
        queryParams.setResultPerPage(0);

        webTestClient.post()
                .uri("/client/parties")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(queryParams)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetBlockPartiesWithBlankSortType() {
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("");
        sortOrder.setOrder(null);
        queryParams.setSortOrder(sortOrder);

        webTestClient.post()
                .uri("/client/parties")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(queryParams)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetFleaMarketItemsByGeoLocationSuccess() {
        when(blockPartyClientService.getFleaMarketItemsGeoLocation(any(QueryParams.class)))
                .thenReturn(Mono.just(geoLocation));

        webTestClient.post()
                .uri("/client/parties/geolocation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(queryParams)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemsGeoLocation.class);

        verify(blockPartyClientService, times(1))
                .getFleaMarketItemsGeoLocation(any(QueryParams.class));
    }

    @Test
    void testGetFleaMarketItemsByGeoLocationWithInvalidParams() {
        queryParams.setResultPerPage(101);

        webTestClient.post()
                .uri("/client/parties/geolocation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(queryParams)
                .exchange()
                .expectStatus().isBadRequest();

        verify(blockPartyClientService, never())
                .getFleaMarketItemsGeoLocation(any(QueryParams.class));
    }

    @Test
    void testGetBlockPartiesWithGermanCharacters() {
        queryParams.setSearchKeyWord("Flöhmarkt äöüÄÖÜß");
        when(blockPartyClientService.getBlockParties(any(QueryParams.class)))
                .thenReturn(Mono.just(allItems));

        webTestClient.post()
                .uri("/client/parties")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(queryParams)
                .exchange()
                .expectStatus().isOk();
    }
}
