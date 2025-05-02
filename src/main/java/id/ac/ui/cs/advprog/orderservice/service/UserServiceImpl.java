package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JwtService jwtService;

    @Override
    public UUID getUserIdFromAuthentication(Authentication authentication) {
        String token = extractTokenFromAuthentication(authentication);
        return jwtService.extractUserId(token);
    }

    private String extractTokenFromAuthentication(Authentication authentication) {
        String authHeader = authentication.getCredentials().toString();
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Invalid authentication token");
    }
}