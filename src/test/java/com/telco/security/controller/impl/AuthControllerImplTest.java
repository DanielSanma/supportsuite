package com.telco.security.controller.impl;

import com.telco.security.dto.AuthResponse;
import com.telco.security.dto.LoginRequest;
import com.telco.security.service.IJwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class AuthControllerImplTest {

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private IJwtService jwtService;
    private AuthControllerImpl authController;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        userDetailsService = mock(UserDetailsService.class);
        jwtService = mock(IJwtService.class);
        authController = new AuthControllerImpl(authenticationManager, userDetailsService, jwtService);
    }

    @Test
    void testLogin_successfulAuthentication_returnsToken() {
        // Arrange
        String username = "testuser";
        String password = "testpass";
        String fakeToken = "mocked-jwt-token";

        LoginRequest loginRequest = new LoginRequest(username, password);
        UserDetails mockUserDetails = mock(UserDetails.class);

        when(userDetailsService.loadUserByUsername(username)).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn(fakeToken);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response);
        assertNotNull(response.getBody());
        assertEquals(fakeToken, response.getBody().token());

        // Verifica que authenticate fue llamado correctamente
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        UsernamePasswordAuthenticationToken token = captor.getValue();
        assertEquals(username, token.getPrincipal());
        assertEquals(password, token.getCredentials());

        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtService).generateToken(mockUserDetails);
    }
}

