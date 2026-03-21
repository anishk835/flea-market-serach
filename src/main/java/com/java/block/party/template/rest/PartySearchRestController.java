package com.java.block.party.template.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.block.party.model.PartySearchResponse;
import com.java.block.party.model.QueryParams;
import com.java.block.party.model.SortOrder;
import com.java.block.party.template.exception.BlockPartySearchException;
import com.java.block.party.template.service.PartySearchService;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
public class PartySearchRestController {

    @Autowired
    private PartySearchService partySearchService;

    @GetMapping("/")
    public String home() {
        return "Hello World!";
    }

    @PostMapping("/parties")
    public PartySearchResponse getBlockParties(@Valid @RequestBody QueryParams partySearchRequest) {
        SortOrder sortOrder = partySearchRequest.getSortOrder();
        if (sortOrder != null && (sortOrder.getType().isBlank() || sortOrder.getOrder() == null)) {
            throw new BlockPartySearchException("sort order does not contain proper details.");
        }
        return partySearchService.getBlockParties(partySearchRequest);
    }

    @GetMapping("/party/query")
    public PartySearchResponse getBlockPartyDetails(
            @RequestParam(name = "id")
            @Pattern(regexp = "^[0-9]{1,10}$", message = "ID must be numeric and between 1-10 digits")
            String id) {
        return partySearchService.getBlockPartyDetails(id);
    }
}
