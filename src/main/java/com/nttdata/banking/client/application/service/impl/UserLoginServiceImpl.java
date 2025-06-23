package com.nttdata.banking.client.application.service.impl;

import com.nttdata.banking.client.application.service.TokenService;
import com.nttdata.banking.client.application.service.UserLoginService;
import com.nttdata.banking.client.dto.request.LoginRequest;
import com.nttdata.banking.client.exception.UserNotFoundException;
import com.nttdata.banking.client.model.Token;
import com.nttdata.banking.client.infrastructure.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final ClientRepository clientRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Token> login(final LoginRequest loginRequest) {
        return clientRepository.findByEmail(loginRequest.getEmail())
                .switchIfEmpty(Mono.error(new UserNotFoundException("No se encontró el usuario con el email: " + loginRequest.getEmail())))
                .flatMap(client -> {
                    if (!passwordEncoder.matches(loginRequest.getPassword(), client.getPassword())) {
                        return Mono.error(new RuntimeException("Contraseña incorrecta"));
                    }
                    return tokenService.generateToken(client.getClaims());
                });
    }
}