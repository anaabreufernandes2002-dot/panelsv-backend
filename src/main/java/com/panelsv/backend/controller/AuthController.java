package com.panelsv.backend.controller;

import com.panelsv.backend.controller.dto.LoginRequest;
import com.panelsv.backend.controller.dto.LoginResponse;
import com.panelsv.backend.model.User;
import com.panelsv.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> ou = users.findByUsername(req.getUsername());
        if (ou.isEmpty()) {
            return ResponseEntity
                    .status(401)
                    .body(new LoginResponse("invalid", "Invalid credentials"));
        }

        User u = ou.get();
        if (!encoder.matches(req.getPassword(), u.getPassword())) {
            return ResponseEntity
                    .status(401)
                    .body(new LoginResponse("invalid", "Invalid credentials"));
        }

        return ResponseEntity.ok(new LoginResponse("ok", "Login successful"));
    }

    // REGISTER (vendedoras)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest req) {
        if (users.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new LoginResponse("exists", "User already exists"));
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(encoder.encode(req.getPassword()));
        // por padrão todas do /register são SELLER
        u.setRole(com.panelsv.backend.model.Role.SELLER);
        users.save(u);

        return ResponseEntity.ok(new LoginResponse("created", "User created"));
    }

    // RESET PASSWORD (usa o mesmo LoginRequest: username + password)
    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody LoginRequest req) {
        Optional<User> ou = users.findByUsername(req.getUsername());
        if (ou.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body(new LoginResponse("not_found", "User not found"));
        }

        User u = ou.get();
        u.setPassword(encoder.encode(req.getPassword()));
        users.save(u);

        return ResponseEntity.ok(new LoginResponse("reset", "Password updated"));
    }
}
