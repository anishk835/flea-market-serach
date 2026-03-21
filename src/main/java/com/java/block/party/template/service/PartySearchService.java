package com.java.block.party.template.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.java.block.party.model.AllItems;
import com.java.block.party.model.ItemDetails;
import com.java.block.party.model.PartySearchResponse;
import com.java.block.party.model.QueryParams;

@Service
@CacheConfig(cacheNames = "blockParty")
public class PartySearchService {

    @Autowired
    private FleaMarketRestService fleaMarketRestService;

    @Cacheable(key = "T(java.lang.String).valueOf(#partySearchRequest.hashCode())",
              value = "partySearchList",
              unless = "#result == null")
    public PartySearchResponse getBlockParties(QueryParams partySearchRequest) {
        AllItems items = fleaMarketRestService.getBlockParties(partySearchRequest);
        PartySearchResponse partySearchResponse = new PartySearchResponse();
        partySearchResponse.setItems(items.getIndex());
        partySearchResponse.setResults(items.getResults());
        partySearchResponse.setSortOrder(partySearchRequest.getSortOrder());
        return partySearchResponse;
    }

    @Cacheable(key = "T(java.lang.String).valueOf(#id.hashCode())",
              value = "partyDetails",
              unless = "#result == null")
    public PartySearchResponse getBlockPartyDetails(String id) {
        ItemDetails itemDetails = fleaMarketRestService.getBlockPartyDetails(id);
        PartySearchResponse partySearchResponse = new PartySearchResponse();
        partySearchResponse.setItem(itemDetails.getItem());
        return partySearchResponse;
    }
}
