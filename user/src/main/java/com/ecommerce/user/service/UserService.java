package com.ecommerce.user.service;

import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    public Optional<UserResponse> fetchUser(String id) {
        return userRepository.findById(id).map(this::mapToUserResponse);
    }

    @Transactional
    public void addUser(UserRequest userRequest) {
        User user = new User();
        updateUserFromRequest(user, userRequest);
        userRepository.save(user);
    }

    @Transactional
    public boolean updateUser(String id, UserRequest userRequest) {
        return userRepository.findById(id).map(existingUser -> {
            updateUserFromRequest(existingUser, userRequest);
            userRepository.save(existingUser);
            return true;
        }).orElse(false);
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        if (userRequest.getAddress() == null) {
            return;
        }

        Address address = user.getAddress() == null ? new Address() : user.getAddress();
        address.setStreet(userRequest.getAddress().getStreet());
        address.setCity(userRequest.getAddress().getCity());
        address.setState(userRequest.getAddress().getState());
        address.setCountry(userRequest.getAddress().getCountry());
        address.setZipCode(userRequest.getAddress().getZipCode());
        user.setAddress(address);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());

        if (user.getAddress() != null) {
            AddressDTO address = new AddressDTO();
            address.setStreet(user.getAddress().getStreet());
            address.setCity(user.getAddress().getCity());
            address.setState(user.getAddress().getState());
            address.setCountry(user.getAddress().getCountry());
            address.setZipCode(user.getAddress().getZipCode());
            response.setAddress(address);
        }

        return response;
    }
}
