package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetUserIdFromAuthentication_Success() {
        UUID expectedUserId = UUID.randomUUID();
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMUBleGFtcGxlLmNvbSIsInVzZXJJZCI6IjEyMzQ1Njc4LTkwMTItNDU2Ny04ODg4LTEyMzQ1NmFiY2RlZiJ9.token";
        String extractedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMUBleGFtcGxlLmNvbSIsInVzZXJJZCI6IjEyMzQ1Njc4LTkwMTItNDU2Ny04ODg4LTEyMzQ1NmFiY2RlZiJ9.token";

        when(authentication.getCredentials()).thenReturn(token);
        when(jwtService.extractAdminId(extractedToken)).thenReturn(expectedUserId.toString());

        UUID result = userService.getUserIdFromAuthentication(authentication);

        assertEquals(expectedUserId, result);
        verify(authentication).getCredentials();
        verify(jwtService).extractAdminId(extractedToken);
    }

        @Test    void testGetUserIdFromAuthentication_InvalidToken() {        when(authentication.getCredentials()).thenReturn("InvalidToken");        assertThrows(RuntimeException.class, () -> userService.getUserIdFromAuthentication(authentication));        verify(authentication).getCredentials();        verify(jwtService, never()).extractAdminId(anyString());    }

        @Test    void testGetUserIdFromAuthentication_NullToken() {        when(authentication.getCredentials()).thenReturn(null);        assertThrows(RuntimeException.class, () -> userService.getUserIdFromAuthentication(authentication));        verify(authentication).getCredentials();        verify(jwtService, never()).extractAdminId(anyString());    }
}