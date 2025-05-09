package com.matchango.scoutingservice.infrastructure.web;

import com.matchango.scoutingservice.application.AuthService;
import com.matchango.scoutingservice.infrastructure.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public ResponseEntity<Void> redirectToKeycloakRegister() {
        String url = authService.getKeycloakRegistrationUrl();
        return ResponseEntity.status(302).location(URI.create(url)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginUser(request));
    }
}
