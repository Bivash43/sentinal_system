package com.example.sentinal_backend.service;

import com.example.sentinal_backend.dto.request.UserCreateRequest;
import com.example.sentinal_backend.dto.request.UserUpdateRequest;
import com.example.sentinal_backend.dto.response.UserResponse;
import com.example.sentinal_backend.model.AppUser;
import com.example.sentinal_backend.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true);

        return toResponse(appUserRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listAll() {
        return appUserRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getById(String id) {
        return toResponse(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public UserResponse getByUsername(String username) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(appUser);
    }

    @Transactional
    public UserResponse update(String id, UserUpdateRequest request) {
        AppUser user = findEntityById(id);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        return toResponse(appUserRepository.save(user));
    }

    @Transactional
    public void delete(String id) {
        AppUser user = findEntityById(id);
        appUserRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public AppUser findByUsernameOrThrow(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private AppUser findEntityById(String id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.isEnabled(), user.getCreatedAt());
    }
}
