package com.example.sentinal_backend.service;

import com.example.sentinal_backend.dto.request.RoleCreateRequest;
import com.example.sentinal_backend.dto.request.RoleUpdateRequest;
import com.example.sentinal_backend.dto.response.RoleResponse;
import com.example.sentinal_backend.model.AppRole;
import com.example.sentinal_backend.repository.AppRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final AppRoleRepository appRoleRepository;

    @Transactional
    public RoleResponse create(RoleCreateRequest request) {
        String roleName = normalizeRoleName(request.getName());
        if (appRoleRepository.existsByName(roleName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists");
        }
        AppRole role = new AppRole();
        role.setName(roleName);
        role.setDescription(request.getDescription());
        role.setActive(request.getActive() == null || request.getActive());
        return toResponse(appRoleRepository.save(role));
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listAll() {
        return appRoleRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse getById(String id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Transactional
    public RoleResponse update(String id, RoleUpdateRequest request) {
        AppRole role = findByIdOrThrow(id);
        if (request.getName() != null && !request.getName().isBlank()) {
            String updatedName = normalizeRoleName(request.getName());
            if (!updatedName.equals(role.getName()) && appRoleRepository.existsByName(updatedName)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists");
            }
            role.setName(updatedName);
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            role.setActive(request.getActive());
        }
        return toResponse(appRoleRepository.save(role));
    }

    @Transactional
    public void delete(String id) {
        AppRole role = findByIdOrThrow(id);
        appRoleRepository.delete(role);
    }

    private AppRole findByIdOrThrow(String id) {
        return appRoleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
    }

    private String normalizeRoleName(String name) {
        return name.trim().toUpperCase(Locale.ROOT);
    }

    private RoleResponse toResponse(AppRole role) {
        return new RoleResponse(role.getId(), role.getName(), role.getDescription(), role.isActive(), role.getCreatedAt());
    }
}
