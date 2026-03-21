package com.java.block.party.template.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.block.party.model.*;
import com.java.block.party.template.exception.BlockPartySearchException;
import com.java.block.party.template.service.PartySearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PartySearchRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PartySearchService partySearchService;

    private PartySearchResponse partySearchResponse;
    private QueryParams queryParams;

    @BeforeEach
    void setUp() {
        partySearchResponse = new PartySearchResponse();
        Results results = new Results();
        Index index1 = new Index();
        Index index2 = new Index();
        partySearchResponse.setResults(results);
        partySearchResponse.setItems(Arrays.asList(index1, index2));

        queryParams = new QueryParams();
        queryParams.setSearchKeyWord("Flohmarkt");
        queryParams.setDistrict("Berlin");
        queryParams.setResultPerPage(20);
    }

    @Test
    void testHomeEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }

    @Test
    void testGetBlockPartiesSuccess() throws Exception {
        when(partySearchService.getBlockParties(any(QueryParams.class)))
                .thenReturn(partySearchResponse);

        mockMvc.perform(post("/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryParams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.results").exists());

        verify(partySearchService, times(1)).getBlockParties(any(QueryParams.class));
    }

    @Test
    void testGetBlockPartiesWithInvalidSearchKeyword() throws Exception {
        queryParams.setSearchKeyWord("<script>alert('xss')</script>");

        mockMvc.perform(post("/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryParams)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Validation failed")));

        verify(partySearchService, never()).getBlockParties(any(QueryParams.class));
    }

    @Test
    void testGetBlockPartiesWithTooLongSearchKeyword() throws Exception {
        queryParams.setSearchKeyWord("a".repeat(101));

        mockMvc.perform(post("/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryParams)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Validation failed")));
    }

    @Test
    void testGetBlockPartiesWithInvalidResultPerPage() throws Exception {
        queryParams.setResultPerPage(0);

        mockMvc.perform(post("/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryParams)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBlockPartiesWithResultPerPageTooLarge() throws Exception {
        queryParams.setResultPerPage(101);

        mockMvc.perform(post("/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryParams)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBlockPartiesWithInvalidSortType() throws Exception {
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("invalid");
        sortOrder.setOrder(Order.ASC);
        queryParams.setSortOrder(sortOrder);

        mockMvc.perform(post("/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryParams)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBlockPartiesWithBlankSortTypeThrowsException() throws Exception {
        SortOrder sortOrder = new SortOrder();
        sortOrder.setType("");
        sortOrder.setOrder(null);
        queryParams.setSortOrder(sortOrder);

        mockMvc.perform(post("/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryParams)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("sort order does not contain proper details."));
    }

    @Test
    void testGetBlockPartyDetailsSuccess() throws Exception {
        when(partySearchService.getBlockPartyDetails(eq("123")))
                .thenReturn(partySearchResponse);

        mockMvc.perform(get("/party/query")
                        .param("id", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").exists());

        verify(partySearchService, times(1)).getBlockPartyDetails("123");
    }

    @Test
    void testGetBlockPartyDetailsWithInvalidId() throws Exception {
        mockMvc.perform(get("/party/query")
                        .param("id", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Validation failed")));

        verify(partySearchService, never()).getBlockPartyDetails(anyString());
    }

    @Test
    void testGetBlockPartyDetailsWithIdTooLong() throws Exception {
        mockMvc.perform(get("/party/query")
                        .param("id", "12345678901"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBlockPartiesWithMalformedJson() throws Exception {
        mockMvc.perform(post("/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }

    @Test
    void testGetBlockPartiesWhenServiceThrowsException() throws Exception {
        when(partySearchService.getBlockParties(any(QueryParams.class)))
                .thenThrow(new BlockPartySearchException("Service unavailable"));

        mockMvc.perform(post("/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryParams)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Service unavailable"));
    }

    @Test
    void testGetBlockPartiesWithGermanCharacters() throws Exception {
        queryParams.setSearchKeyWord("Flöhmarkt äöüÄÖÜß");
        when(partySearchService.getBlockParties(any(QueryParams.class)))
                .thenReturn(partySearchResponse);

        mockMvc.perform(post("/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryParams)))
                .andExpect(status().isOk());
    }
}
