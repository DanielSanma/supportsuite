package com.telco.users.service.impl;

import com.telco.users.model.Rol;
import com.telco.users.model.User;
import com.telco.users.repository.IUserRepository;
import com.telco.users.repository.RolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private IUserRepository userRepository;
    private RolRepository rolRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(IUserRepository.class);
        rolRepository = mock(RolRepository.class);
        userService = new UserServiceImpl(userRepository, rolRepository);
    }

    @Test
    void testLoadUserByUsername_found() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername(email);
        assertEquals(user, result);
    }

    @Test
    void testLoadUserByUsername_notFound() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void testUpdateUserRole_success() {
        Long userId = 1L;
        Long roleId = 2L;

        User user = new User();
        user.setId(userId);

        Rol newRole = new Rol();
        newRole.setId(roleId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rolRepository.findById(roleId)).thenReturn(Optional.of(newRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUserRole(userId, roleId);

        assertEquals(newRole, updatedUser.getRol());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserRole_userNotFound() {
        Long userId = 1L;
        Long roleId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(userId, roleId));
    }

    @Test
    void testUpdateUserRole_roleNotFound() {
        Long userId = 1L;
        Long roleId = 2L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rolRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(userId, roleId));
    }

    @Test
    void testDeleteUser_success() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUser_notFound() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));
    }

    

    
}
