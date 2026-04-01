package com.example.sentinal_backend.controller;

import com.example.sentinal_backend.dto.request.RoleCreateRequest;
import com.example.sentinal_backend.dto.request.RoleUpdateRequest;
import com.example.sentinal_backend.dto.response.RoleResponse;
import com.example.sentinal_backend.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("@seededAdminAccessGuard.isSeededAdmin(authentication)")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> list() {
        return ResponseEntity.ok(roleService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> update(@PathVariable String id, @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
