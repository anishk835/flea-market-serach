package com.java.block.party.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlBuilderTest {

    @InjectMocks
    private UrlBuilder urlBuilder;

    @Mock
    private Property property;

    @Mock
    private Property.DefaultProperty defaultProperty;

    @BeforeEach
    void setUp() {
        when(property.getDefaultProperty()).thenReturn(defaultProperty);
    }

    @Test
    void testGetAllItemsJsonUrl() {
        when(defaultProperty.getBaseUrl()).thenReturn("https://example.com/");
        when(defaultProperty.getItemsSuffix()).thenReturn("items/all");
        when(defaultProperty.getJson()).thenReturn(".json");
        when(defaultProperty.getQueryString()).thenReturn("?q=");

        String url = urlBuilder.getAllItemsJsonUrl();

        assertThat(url).contains("https://example.com/");
        assertThat(url).contains("items/all");
        assertThat(url).contains(".json");
        assertThat(url).contains("?q=");
    }

    @Test
    void testGetItemJsonUrl() {
        when(defaultProperty.getBaseUrl()).thenReturn("https://example.com/");
        when(defaultProperty.getItemSuffix()).thenReturn("item/");
        when(defaultProperty.getJson()).thenReturn(".json");

        String url = urlBuilder.getItemJsonUrl();

        assertThat(url).contains("https://example.com/");
        assertThat(url).contains("item/");
        assertThat(url).contains("{itemId}");
        assertThat(url).contains(".json");
    }

    @Test
    void testGetItemsGeoJsonUrl() {
        when(defaultProperty.getBaseUrl()).thenReturn("https://example.com/");
        when(defaultProperty.getItemsSuffix()).thenReturn("items/all");
        when(defaultProperty.getGeojson()).thenReturn(".geojson");
        when(defaultProperty.getQueryString()).thenReturn("?q=");

        String url = urlBuilder.getItemsGeoJsonUrl();

        assertThat(url).contains("https://example.com/");
        assertThat(url).contains("items/all");
        assertThat(url).contains(".geojson");
        assertThat(url).contains("?q=");
    }
}
