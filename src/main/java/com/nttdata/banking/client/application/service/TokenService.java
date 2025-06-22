package com.nttdata.banking.client.application.service;

import com.nttdata.banking.client.model.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

public interface TokenService {

    Mono<Token> generateToken(final Map<String, Object> claims);

    Mono<Token> generateToken(final Map<String, Object> claims, final String refreshToken);

    Mono<UsernamePasswordAuthenticationToken> getAuthentication(final String token);

    Mono<Void> verifyAndValidate(final String jwt);

    Mono<Void> verifyAndValidate(final Set<String> jwts);

    Mono<Jws<Claims>> getClaims(final String jwt);

    Mono<Claims> getPayload(final String jwt);

    Mono<String> getId(final String jwt);

}