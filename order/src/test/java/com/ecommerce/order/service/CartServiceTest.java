package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.client.UserClient;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserClient userClient;
    @Mock
    private ProductClient productClient;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartItemRepository, userClient, productClient);
    }

    @Test
    void addsAvailableProductUsingPriceFromProductService() {
        CartItemRequest request = request(10L, 2);
        ProductResponse product = product(10L, "149.90", 5);

        when(userClient.getUser("user-1")).thenReturn(Optional.of(new UserResponse()));
        when(productClient.getProduct(10L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId("user-1", 10L)).thenReturn(null);

        boolean added = cartService.addToCart("user-1", request);

        ArgumentCaptor<CartItem> savedItem = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(savedItem.capture());
        assertThat(added).isTrue();
        assertThat(savedItem.getValue().getPrice()).isEqualByComparingTo("149.90");
        assertThat(savedItem.getValue().getQuantity()).isEqualTo(2);
    }

    @Test
    void rejectsRequestWhenUserDoesNotExist() {
        CartItemRequest request = request(10L, 1);
        when(userClient.getUser("missing-user")).thenReturn(Optional.empty());

        boolean added = cartService.addToCart("missing-user", request);

        assertThat(added).isFalse();
        verify(productClient, never()).getProduct(10L);
        verify(cartItemRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsQuantityGreaterThanAvailableStock() {
        CartItemRequest request = request(10L, 3);
        ProductResponse product = product(10L, "149.90", 4);
        CartItem existingItem = new CartItem();
        existingItem.setQuantity(2);

        when(userClient.getUser("user-1")).thenReturn(Optional.of(new UserResponse()));
        when(productClient.getProduct(10L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId("user-1", 10L)).thenReturn(existingItem);

        boolean added = cartService.addToCart("user-1", request);

        assertThat(added).isFalse();
        verify(cartItemRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private CartItemRequest request(Long productId, int quantity) {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        return request;
    }

    private ProductResponse product(Long id, String price, int stockQuantity) {
        ProductResponse product = new ProductResponse();
        product.setId(id);
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(stockQuantity);
        product.setActive(true);
        return product;
    }
}
