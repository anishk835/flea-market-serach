package com.java.block.party.client.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.block.party.client.service.BlockPartyClientService;
import com.java.block.party.model.AllItems;
import com.java.block.party.model.ItemDetails;
import com.java.block.party.model.QueryParams;
import com.java.block.party.model.SortOrder;
import com.java.block.party.model.geo.ItemsGeoLocation;
import com.java.block.party.template.exception.BlockPartySearchException;

import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@Validated
public class BlockPartyClientRestHandler {

    private static final String GET_PARTY_DETAILS_BY_ID_V1 = "client/party/query";
    private static final String GET_BLOCK_PARTIES_V1 = "client/parties";
    private static final String GET_BLOCK_PARTIES_GEO_LOCATION_V1 = "client/parties/geolocation";

    @Autowired
    private BlockPartyClientService blockPartyClientService;

    @GetMapping(GET_PARTY_DETAILS_BY_ID_V1)
    public Mono<ItemDetails> getBlockPartyDetails(
            @RequestParam(name = "id", required = true)
            @Pattern(regexp = "^[0-9]{1,10}$", message = "ID must be numeric and between 1-10 digits")
            String id) {
        return blockPartyClientService.getBlockPartyDetails(id);
    }

    @PostMapping(GET_BLOCK_PARTIES_V1)
    public Mono<AllItems> getBlockParties(@Valid @RequestBody QueryParams partySearchRequest) {
        validateSortOrder(partySearchRequest);
        return blockPartyClientService.getBlockParties(partySearchRequest);
    }

    @PostMapping(GET_BLOCK_PARTIES_GEO_LOCATION_V1)
    public Mono<ItemsGeoLocation> getFleaMarketItemsByGeoLocation(@Valid @RequestBody QueryParams partySearchRequest) {
        validateSortOrder(partySearchRequest);
        return blockPartyClientService.getFleaMarketItemsGeoLocation(partySearchRequest);
    }

    private void validateSortOrder(QueryParams partySearchRequest) {
        SortOrder sortOrder = partySearchRequest.getSortOrder();
        if (sortOrder != null && (sortOrder.getType().isBlank() || sortOrder.getOrder() == null)) {
            throw new BlockPartySearchException("sort order does not contain proper details.");
        }
    }

}
