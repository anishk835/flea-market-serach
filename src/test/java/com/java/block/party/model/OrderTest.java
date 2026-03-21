package com.java.block.party.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void testAscendingOrder() {
        Order order = Order.ASC;
        assertThat(order).isEqualTo(Order.ASC);
        assertThat(order.name()).isEqualTo("ASC");
    }

    @Test
    void testDescendingOrder() {
        Order order = Order.DESC;
        assertThat(order).isEqualTo(Order.DESC);
        assertThat(order.name()).isEqualTo("DESC");
    }

    @Test
    void testValueOf() {
        Order ascOrder = Order.valueOf("ASC");
        Order descOrder = Order.valueOf("DESC");

        assertThat(ascOrder).isEqualTo(Order.ASC);
        assertThat(descOrder).isEqualTo(Order.DESC);
    }

    @Test
    void testValues() {
        Order[] orders = Order.values();
        assertThat(orders).hasSize(2);
        assertThat(orders).contains(Order.ASC, Order.DESC);
    }
}
