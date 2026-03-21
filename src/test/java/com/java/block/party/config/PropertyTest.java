package com.java.block.party.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyTest {

    @Test
    void testPropertyGettersAndSetters() {
        Property property = new Property();
        Property.DefaultProperty defaultProperty = property.getDefaultProperty();

        defaultProperty.setBaseUrl("https://example.com/");
        defaultProperty.setItemsSuffix("items/all");
        defaultProperty.setItemSuffix("item/");
        defaultProperty.setQueryString("?q=");
        defaultProperty.setJson(".json");
        defaultProperty.setGeojson(".geojson");

        assertThat(defaultProperty.getBaseUrl()).isEqualTo("https://example.com/");
        assertThat(defaultProperty.getItemsSuffix()).isEqualTo("items/all");
        assertThat(defaultProperty.getItemSuffix()).isEqualTo("item/");
        assertThat(defaultProperty.getQueryString()).isEqualTo("?q=");
        assertThat(defaultProperty.getJson()).isEqualTo(".json");
        assertThat(defaultProperty.getGeojson()).isEqualTo(".geojson");
    }

    @Test
    void testPropertyHasDefaultPropertyInstance() {
        Property property = new Property();

        assertThat(property.getDefaultProperty()).isNotNull();
    }
}
