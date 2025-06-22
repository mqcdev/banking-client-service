package com.nttdata.banking.client.application.service.impl;

import com.nttdata.banking.client.application.service.TokenService;
import com.nttdata.banking.client.config.TokenConfigurationParameter;
import com.nttdata.banking.client.model.Token;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenConfigurationParameter tokenConfig;

    public TokenServiceImpl(TokenConfigurationParameter tokenConfig) {
        this.tokenConfig = tokenConfig;
    }

    @Override
    public Mono<Token> generateToken(Map<String, Object> claims) {
        // Generar access token
        long accessTokenExpiresAt = System.currentTimeMillis() + tokenConfig.getAccessTokenExpireMinute() * 60 * 1000L;
        String jwtId = UUID.randomUUID().toString();
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setId(jwtId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(accessTokenExpiresAt))
                .signWith(tokenConfig.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();

        // Generar refresh token
        long refreshTokenExpiresAt = System.currentTimeMillis() + tokenConfig.getRefreshTokenExpireDay() * 24 * 60 * 60 * 1000L;
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = Jwts.builder()
                .setSubject((String) claims.getOrDefault("EMAIL", claims.get("email")))
                .setId(refreshTokenId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(refreshTokenExpiresAt))
                .signWith(tokenConfig.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();

        // Crear el objeto Token completo
        Token token = Token.builder()
                .accessToken(jwt)
                .accessTokenExpiresAt(accessTokenExpiresAt)
                .refreshToken(refreshToken)
                .refreshTokenExpiresAt(refreshTokenExpiresAt)
                .build();

        return Mono.just(token);
    }

    @Override
    public Mono<Token> generateToken(Map<String, Object> claims, String refreshToken) {
        // Generar solo un nuevo access token manteniendo el mismo refresh token
        long accessTokenExpiresAt = System.currentTimeMillis() + tokenConfig.getAccessTokenExpireMinute() * 60 * 1000L;
        String jwtId = UUID.randomUUID().toString();
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setId(jwtId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(accessTokenExpiresAt))
                .signWith(tokenConfig.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();

        // Calcular la fecha de expiración del refresh token (desde ahora + días configurados)
        long refreshTokenExpiresAt = System.currentTimeMillis() + tokenConfig.getRefreshTokenExpireDay() * 24 * 60 * 60 * 1000L;

        Token token = Token.builder()
                .accessToken(jwt)
                .accessTokenExpiresAt(accessTokenExpiresAt)
                .refreshToken(refreshToken)
                .refreshTokenExpiresAt(refreshTokenExpiresAt)
                .build();

        return Mono.just(token);
    }

    @Override
    public Mono<UsernamePasswordAuthenticationToken> getAuthentication(String token) {
        return getClaims(token)
                .map(jws -> {
                    Claims claims = jws.getBody();
                    String username = claims.getSubject();
                    return new UsernamePasswordAuthenticationToken(username, null, null);
                })
                .onErrorResume(e -> Mono.empty());
    }

    @Override
    public Mono<Void> verifyAndValidate(String jwt) {
        if (jwt == null) {
            return Mono.empty();
        }
        return getClaims(jwt)
                .onErrorResume(e -> Mono.empty())
                .then();
    }

    @Override
    public Mono<Void> verifyAndValidate(Set<String> jwts) {
        if (jwts == null || jwts.isEmpty()) {
            return Mono.empty();
        }

        return Mono.when(
                jwts.stream()
                        .filter(jwt -> jwt != null)
                        .map(jwt -> verifyAndValidate(jwt)
                                .onErrorResume(e -> Mono.empty()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Mono<Jws<Claims>> getClaims(String jwt) {
        if (jwt == null) {
            return Mono.error(new JwtException("JWT token is null"));
        }
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(tokenConfig.getPublicKey())
                    .build()
                    .parseClaimsJws(jwt);
            return Mono.just(claims);
        } catch (JwtException e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Claims> getPayload(String jwt) {
        if (jwt == null) {
            return Mono.empty();
        }
        return getClaims(jwt)
                .map(Jws::getBody)
                .onErrorResume(e -> Mono.empty());
    }

    @Override
    public Mono<String> getId(String jwt) {
        if (jwt == null) {
            return Mono.empty();
        }
        return getPayload(jwt)
                .map(Claims::getId)
                .onErrorResume(e -> Mono.empty());
    }
}