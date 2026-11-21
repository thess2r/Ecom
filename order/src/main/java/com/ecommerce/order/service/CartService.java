package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.client.UserClient;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserClient userClient;
    private final ProductClient productClient;


    public boolean addToCart(String userId, CartItemRequest request) {
        if (request.getProductId() == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            return false;
        }

        if (userClient.getUser(userId).isEmpty()) {
            return false;
        }

        ProductResponse product = productClient.getProduct(request.getProductId()).orElse(null);
        if (product == null || !Boolean.TRUE.equals(product.getActive()) || product.getPrice() == null) {
            return false;
        }

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId,request.getProductId());
        int requestedTotalQuantity = request.getQuantity();
        if (existingCartItem != null) {
            requestedTotalQuantity += existingCartItem.getQuantity();
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < requestedTotalQuantity) {
            return false;
        }

        if (existingCartItem != null){
            //update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(product.getPrice());
            cartItemRepository.save(existingCartItem);
        }
        else {
            //create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice());
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public boolean deleteItemFromCart(String userId, Long productId) {
        CartItem cartItem =  cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem != null){
            cartItemRepository.delete(cartItem);
            return  true;
        }
        return  false;

    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }


}
