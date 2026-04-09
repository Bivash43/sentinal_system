package com.example.sentinal_backend.controller;

import com.example.sentinal_backend.dto.request.LoginRequest;
import com.example.sentinal_backend.model.UserRole;
import com.example.sentinal_backend.model.AppUser;
import com.example.sentinal_backend.model.RefreshToken;
import com.example.sentinal_backend.security.JwtService;
import com.example.sentinal_backend.service.AppUserDetailsService;
import com.example.sentinal_backend.service.RefreshTokenService;
import com.example.sentinal_backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.sentinal_backend.AbstractIntegrationTest;

public class AuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    // We mock AuthenticationProvider to satisfy SecurityConfig
    @MockitoBean
    private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        User principal = new User("testuser", "password123", Collections.emptyList());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, "password123",
                Collections.emptyList());

        UserRole role = UserRole.ANALYST;

        AppUser appUser = new AppUser();
        appUser.setUsername("testuser");
        appUser.setRole(role);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token-123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(userService.findByUsernameOrThrow("testuser")).thenReturn(appUser);
        when(jwtService.generateToken(principal, role)).thenReturn("jwt-token-123");
        when(refreshTokenService.createRefreshToken("testuser")).thenReturn(refreshToken);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-123"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void shouldFailLoginValidationWhenFieldsEmpty() throws Exception {
        LoginRequest loginRequest = new LoginRequest();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}
