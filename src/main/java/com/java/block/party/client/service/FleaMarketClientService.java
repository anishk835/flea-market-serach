package com.java.block.party.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.java.block.party.config.UrlBuilder;
import com.java.block.party.model.AllItems;
import com.java.block.party.model.ItemDetails;
import com.java.block.party.model.QueryParams;
import com.java.block.party.model.SortOrder;
import com.java.block.party.model.geo.ItemsGeoLocation;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class FleaMarketClientService {

    @Autowired
    private WebClient webClient;

    @Autowired
    private UrlBuilder urlBuilder;

    public Mono<ItemsGeoLocation> getFleaMarketItemsGeoLocation(final QueryParams params) {
        String url = buildUrlWithParams(urlBuilder.getItemsGeoJsonUrl(), params);
        log.debug("geo based items details url is : {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(ItemsGeoLocation.class);
    }

    public Mono<AllItems> getBlockParties(final QueryParams params) {
        String url = buildUrlWithParams(urlBuilder.getAllItemsJsonUrl(), params);
        log.debug("items url with query string is : {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(AllItems.class);
    }

    public Mono<ItemDetails> getBlockPartyDetails(final String id) {
        String url = UriComponentsBuilder.fromUriString(urlBuilder.getItemJsonUrl())
                .buildAndExpand(id).toUriString();
        log.debug("item details url is : {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(ItemDetails.class);
    }

    private String buildUrlWithParams(String baseUrl, QueryParams params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);

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

        return builder.build().toUriString();
    }
}
