package com.telco.security.service.impl;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        // Inject secretKey and jwtExpiration using reflection
        setField(jwtService, "secretKey", "0123456789012345678901234567890123456789012345678901234567890123");
        setField(jwtService, "jwtExpiration", 3600000L); // 1 hour

        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void testGenerateTokenAndValidate() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(userDetails.getUsername(), extractedUsername);

        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void testTokenIsInvalidWithDifferentUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("otheruser", "password", Collections.emptyList());

        boolean isValid = jwtService.isTokenValid(token, otherUser);
        assertFalse(isValid);
    }

    // Utility method to inject private fields via reflection
    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Could not set field " + fieldName, e);
        }
    }
}

