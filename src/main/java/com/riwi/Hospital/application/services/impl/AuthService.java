package com.riwi.Hospital.application.services.impl;

// Importaciones necesarias para el funcionamiento del servicio

import com.riwi.Hospital.application.dtos.requests.LoginRequest;
import com.riwi.Hospital.domain.entities.User;
import com.riwi.Hospital.domain.enums.Role;
import com.riwi.Hospital.infrastructure.helpers.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service // Anotación que marca esta clase como un servicio de Spring
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager; // Manejador de autenticación inyectado

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Implementación personalizada de UserDetailsService

    @Autowired
    private JwtUtil jwtUtil; // Utilidad para el manejo de JWT

    // Método para autenticar al usuario y generar un token
    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getDocument(),
                        loginRequest.getPassword()
                )
        );

        User user = (User)  userDetailsService.loadUserByUsername(loginRequest.getDocument());
        return jwtUtil.generateToken(user);
    }

    // Método para obtener el rol del usuario actualmente autenticado
    public Role getCurrentUserRole() {
        // Obtiene el nombre de usuario del usuario autenticado
        String username = getCurrentAuthenticatedUsername();
        // Carga el usuario desde el UserDetailsService
        User user = (User) userDetailsService.loadUserByUsername(username);
        // Retorna el rol del usuario
        return user.getRole();
    }

    // Método privado para obtener el nombre de usuario del contexto de seguridad
    private String getCurrentAuthenticatedUsername() {
        // Obtiene la autenticación actual desde el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Retorna el nombre de usuario si la autenticación es válida, de lo contrario retorna null
        return authentication != null ? authentication.getName() : null;
    }

    // Método para verificar si el usuario tiene un permiso específico
    public boolean hasPermission(String permission) {
        // Obtiene el rol del usuario actual
        Role role = getCurrentUserRole();
        // Verifica si el rol tiene el permiso solicitado
        return role.getPermissions().contains(permission);
    }
}
