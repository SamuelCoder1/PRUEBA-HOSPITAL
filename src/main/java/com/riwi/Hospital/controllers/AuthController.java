package com.riwi.Hospital.controllers;

import com.riwi.Hospital.application.dtos.requests.LoginRequest;
import com.riwi.Hospital.application.dtos.requests.UserWithoutId;
import com.riwi.Hospital.application.services.impl.AuthService;
import com.riwi.Hospital.domain.entities.User;
import com.riwi.Hospital.domain.ports.service.IUserService;
import com.riwi.Hospital.infrastructure.persistence.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private IUserService userService;

    @Autowired
    UserRepository userRepository;

    // Endpoint para iniciar sesión
    @Operation(summary = "Login a user", description = "Autentica un usuario y devuelve un JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, JWT retornado"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String jwtToken = authService.login(loginRequest);
        return ResponseEntity.ok(jwtToken);
    }

    @Operation(summary =  "Register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "User with document already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserWithoutId userDTO) {
        try {
            if (userRepository.existsByDocument(userDTO.getDocument())) {
                return ResponseEntity.status(409).body(null);
            }

            User user = userService.register(userDTO);
            return ResponseEntity.status(201).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
