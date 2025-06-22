package com.nttdata.banking.client.application.service.impl;

import com.nttdata.banking.client.application.service.RefreshTokenService;
import com.nttdata.banking.client.application.service.TokenService;
import com.nttdata.banking.client.dto.request.TokenRefreshRequest;
import com.nttdata.banking.client.exception.UserNotFoundException;
import com.nttdata.banking.client.exception.UserStatusNotValidException;
import com.nttdata.banking.client.infrastructure.ClientRepository;
import com.nttdata.banking.client.model.Client;
import com.nttdata.banking.client.model.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final ClientRepository clientRepository;
    private final TokenService tokenService;

    @Override
    public Mono<Token> refreshToken(final TokenRefreshRequest tokenRefreshRequest) {
        return tokenService.getPayload(tokenRefreshRequest.getRefreshToken())
                .map(claims -> claims.getSubject())
                .flatMap(email -> clientRepository.findUserEntityByEmail(email)
                        .switchIfEmpty(Mono.error(new UserNotFoundException("No se encontró el usuario con email: " + email)))
                        .flatMap(this::validateUserStatus)
                        .flatMap(user -> tokenService.generateToken(user.getClaims(), tokenRefreshRequest.getRefreshToken()))
                );
    }

    private Mono<Client> validateUserStatus(final Client userEntity) {
        if (Boolean.FALSE.equals(userEntity.getState())) {
            return Mono.error(new UserStatusNotValidException("El usuario no está activo"));
        }
        return Mono.just(userEntity);
    }
}