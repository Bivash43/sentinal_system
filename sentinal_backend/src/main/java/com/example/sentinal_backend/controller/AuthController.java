package com.example.sentinal_backend.controller;

import com.example.sentinal_backend.dto.request.LoginRequest;
import com.example.sentinal_backend.dto.response.LoginResponse;
import com.example.sentinal_backend.service.UserService;
import com.example.sentinal_backend.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        var authentication = authenticationManager.authenticate(authToken);
        var principal = (User) authentication.getPrincipal();
        var appUser = userService.findByUsernameOrThrow(principal.getUsername());
        String token = jwtService.generateToken(principal, appUser.getRole());

        return ResponseEntity.ok(new LoginResponse(token, appUser.getUsername(), appUser.getRole()));
    }
}
