package com.java.block.party.template.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.java.block.party.config.UrlBuilder;
import com.java.block.party.model.AllItems;
import com.java.block.party.model.ItemDetails;
import com.java.block.party.model.QueryParams;
import com.java.block.party.model.SortOrder;
import com.java.block.party.template.exception.BlockPartySearchException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FleaMarketRestService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UrlBuilder urlBuilder;

    public AllItems getBlockParties(final QueryParams params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(urlBuilder.getAllItemsJsonUrl());

        if (params != null) {
            if (params.getSearchKeyWord() != null && !params.getSearchKeyWord().isEmpty()) {
                builder.queryParam("q", params.getSearchKeyWord());
            }
            if (params.getDistrict() != null && !params.getDistrict().isEmpty()) {
                builder.queryParam("bezirk", params.getDistrict());
            }
            builder.queryParam("ipp", params.getResultPerPage());

            SortOrder sortOrder = params.getSortOrder();
            if (sortOrder != null && sortOrder.getType() != null && !sortOrder.getType().isEmpty()
                    && sortOrder.getOrder() != null) {
                builder.queryParam("order", sortOrder.getType() + "+" + sortOrder.getOrder());
                builder.fragment("results");
            } else {
                builder.fragment("searchresults");
            }
        }

        String url = builder.build().toUriString();
        log.debug("items url with query string is : {}", url);
        ResponseEntity<AllItems> response = restTemplate.getForEntity(url, AllItems.class);

        if (response.getBody() == null) {
            log.error("Received null response body for URL: {}", url);
            throw new BlockPartySearchException("Failed to retrieve block parties: empty response from server");
        }

        return response.getBody();
    }

    public ItemDetails getBlockPartyDetails(final String id) {
        String url = UriComponentsBuilder.fromUriString(urlBuilder.getItemJsonUrl())
                .buildAndExpand(id).toUriString();
        log.debug("item details url is : {}", url);
        ResponseEntity<ItemDetails> response = restTemplate.getForEntity(url, ItemDetails.class);

        if (response.getBody() == null) {
            log.error("Received null response body for item ID: {}", id);
            throw new BlockPartySearchException("Failed to retrieve party details: empty response from server");
        }

        return response.getBody();
    }

}
