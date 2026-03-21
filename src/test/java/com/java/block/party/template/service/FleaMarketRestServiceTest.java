package com.java.block.party.template.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.java.block.party.config.UrlBuilder;
import com.java.block.party.model.AllItems;
import com.java.block.party.model.ItemDetails;
import com.java.block.party.model.QueryParams;

@ExtendWith(MockitoExtension.class)
class FleaMarketRestServiceTest {

    private static final String HTTP_LOCALHOST_8080_PARTY_QUERY_ID_321 = "http://localhost:8080/party/query?id=321";
    private static final String HTTP_LOCALHOST_8080_PARTIES_BEZIRK_IPP_10_SEARCHRESULTS = "http://localhost:8080/parties?bezirk=&ipp=10#searchresults";
    private static final String HTTP_LOCALHOST_8080_PARTIES = "http://localhost:8080/parties";

    @InjectMocks
    private FleaMarketRestService fleaMarketRestService;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private UrlBuilder urlBuilder;
    @Mock
    private AllItems allItems;
    @Mock
    private ItemDetails itemDetails;

    @Test
    final void testGetBlockParties() {
        QueryParams params = new QueryParams();
        when(urlBuilder.getAllItemsJsonUrl()).thenReturn(HTTP_LOCALHOST_8080_PARTIES);
        when(restTemplate.getForEntity(anyString(), eq(AllItems.class)))
                .thenReturn(new ResponseEntity<AllItems>(allItems, HttpStatus.OK));
        fleaMarketRestService.getBlockParties(params);
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(AllItems.class));
    }

    @Test
    final void testGetBlockPartyDetails() {
        when(urlBuilder.getItemJsonUrl()).thenReturn(HTTP_LOCALHOST_8080_PARTY_QUERY_ID_321);
        when(restTemplate.getForEntity(anyString(), eq(ItemDetails.class)))
                .thenReturn(new ResponseEntity<ItemDetails>(itemDetails, HttpStatus.OK));
        fleaMarketRestService.getBlockPartyDetails("321");
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(ItemDetails.class));
    }

}
