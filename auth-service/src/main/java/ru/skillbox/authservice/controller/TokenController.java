package ru.skillbox.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.authservice.dto.UserDto;
import ru.skillbox.authservice.repository.UserRepository;
import ru.skillbox.authservice.security.jwt.JwtUtils;

@RestController
@RequestMapping("/token")
public class TokenController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtil;

    private final UserRepository userRepository;

    public TokenController(AuthenticationManager authenticationManager, JwtUtils jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Generate token")
    @PostMapping(value = "/generate")
    public ResponseEntity<?> generateToken(@RequestBody UserDto loginUser)
            throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getName(),
                        loginUser.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = userRepository.findByName(loginUser.getName())
                .map(user -> jwtUtil.generateToken(user.getName()))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        return ResponseEntity.ok(token);
    }
}
