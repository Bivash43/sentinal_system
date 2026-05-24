package com.example.sentinal_backend.auth.controller;

import com.example.sentinal_backend.auth.dto.request.LoginRequest;
import com.example.sentinal_backend.auth.dto.request.TokenRefreshRequest;
import com.example.sentinal_backend.auth.dto.response.LoginResponse;
import com.example.sentinal_backend.auth.dto.response.TokenRefreshResponse;
import com.example.sentinal_backend.user.model.AppUser;
import com.example.sentinal_backend.auth.model.RefreshToken;
import com.example.sentinal_backend.auth.service.JwtService;
import com.example.sentinal_backend.auth.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.example.sentinal_backend.auth.model.AppUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        var authentication = authenticationManager.authenticate(authToken);
        var principal = (AppUserDetails) authentication.getPrincipal();
        var appUser = principal.getAppUser();
        
        String token = jwtService.generateToken(principal, appUser.getRole());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(appUser.getUsername());

        return ResponseEntity.ok(new LoginResponse(token, refreshToken.getToken(), appUser.getUsername(), appUser.getRole()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshTokenService::rotateRefreshToken)
                .map(newRefreshToken -> {
                    AppUser appUser = newRefreshToken.getUser();
                    UserDetails userDetails = new AppUserDetails(appUser);
                    String token = jwtService.generateToken(userDetails, appUser.getRole());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, newRefreshToken.getToken()));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
             refreshTokenService.deleteByUserUsername(userDetails.getUsername());
             return ResponseEntity.ok("Log out successful");
        }
        return ResponseEntity.badRequest().body("No active user session");
    }
}
