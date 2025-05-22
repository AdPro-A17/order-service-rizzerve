package id.ac.ui.cs.advprog.orderservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.UUID;
import java.util.function.Function;
import java.util.List;
import java.util.ArrayList;

@Service
public class JwtService {
    // TODO: Ensure 'jwt.secret' is configured properly in application.properties for production.
    // For tests, a dummy secret is provided in src/test/resources/application.properties.
    // It's crucial that the production secret is strong and kept confidential.
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String extractUsername(String token) {
        // Not implemented yet - for RED phase testing
        return null;
    }

    public UUID extractUserId(String token) {
        String userId = extractClaim(token, claims -> claims.get("userId", String.class));
        return userId != null ? UUID.fromString(userId) : null;
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        // Not implemented yet - for RED phase testing
        return new ArrayList<>();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // TODO: Review JWT parsing and validation logic for security best practices.
        // Consider adding more robust error handling for malformed or expired tokens.
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        // TODO: Ensure the secret key is sufficiently long and complex for SHA256.
        // Consider externalizing secret management (e.g., using environment variables or a secrets manager) for production.
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        // Not implemented yet - for RED phase testing
        return false;
    }
}