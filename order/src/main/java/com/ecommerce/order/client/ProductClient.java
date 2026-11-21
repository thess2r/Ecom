package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${services.product.base-url:http://localhost:8081}")
    private String productServiceBaseUrl;

    public Optional<ProductResponse> getProduct(Long productId) {
        try {
            ProductResponse product = restClientBuilder.build()
                    .get()
                    .uri(productServiceBaseUrl + "/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductResponse.class);
            return Optional.ofNullable(product);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw exception;
        }
    }
}
