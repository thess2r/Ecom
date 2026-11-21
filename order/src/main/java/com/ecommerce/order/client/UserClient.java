package com.ecommerce.order.client;

import com.ecommerce.order.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${services.user.base-url:http://localhost:8082}")
    private String userServiceBaseUrl;

    public Optional<UserResponse> getUser(String userId) {
        try {
            UserResponse user = restClientBuilder.build()
                    .get()
                    .uri(userServiceBaseUrl + "/api/users/{id}", userId)
                    .retrieve()
                    .body(UserResponse.class);
            return Optional.ofNullable(user);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw exception;
        }
    }
}
