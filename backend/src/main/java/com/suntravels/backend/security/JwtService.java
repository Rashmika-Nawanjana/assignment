package com.suntravels.backend.security;

import com.suntravels.backend.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Creates and reads JWT tokens after login.
 */
@Service
public class JwtService
{
    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService( @Value( "${app.jwt.secret}" ) String secret,
                       @Value( "${app.jwt.expiration-ms}" ) long expirationMs )
    {
        this.secretKey = Keys.hmacShaKeyFor( secret.getBytes( StandardCharsets.UTF_8 ) );
        this.expirationMs = expirationMs;
    }

    public String generateToken( String username, Role role )
    {
        Date now = new Date();
        Date expiry = new Date( now.getTime() + expirationMs );

        return Jwts.builder()
                .subject( username )
                .claim( "role", role.name() )
                .issuedAt( now )
                .expiration( expiry )
                .signWith( secretKey )
                .compact();
    }

    public String extractUsername( String token )
    {
        return parseClaims( token ).getSubject();
    }

    public String extractRole( String token )
    {
        return parseClaims( token ).get( "role", String.class );
    }

    public boolean isTokenValid( String token, String expectedUsername )
    {
        String username = extractUsername( token );
        Date expiration = parseClaims( token ).getExpiration();
        return username.equals( expectedUsername ) && expiration.after( new Date() );
    }

    private Claims parseClaims( String token )
    {
        return Jwts.parser()
                .verifyWith( secretKey )
                .build()
                .parseSignedClaims( token )
                .getPayload();
    }
}
