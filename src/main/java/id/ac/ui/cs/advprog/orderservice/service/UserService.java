package id.ac.ui.cs.advprog.orderservice.service;

import org.springframework.security.core.Authentication;
import java.util.UUID;

public interface UserService {
    UUID getUserIdFromAuthentication(Authentication authentication);
}