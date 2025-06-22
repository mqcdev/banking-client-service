package com.nttdata.banking.client.application.service.impl;

import com.nttdata.banking.client.application.service.InvalidTokenService;
import com.nttdata.banking.client.exception.TokenAlreadyInvalidatedException;
import com.nttdata.banking.client.infrastructure.InvalidTokenRepository;
import com.nttdata.banking.client.model.InvalidTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación reactiva de {@link InvalidTokenService} para gestionar tokens inválidos.
 */
@Service
@RequiredArgsConstructor
public class InvalidTokenServiceImpl implements InvalidTokenService {

    private final InvalidTokenRepository invalidTokenRepository;

    @Override
    public Mono<Void> invalidateTokens(Set<String> tokenIds) {
        Set<InvalidTokenEntity> invalidTokenEntities = tokenIds.stream()
                .map(tokenId -> InvalidTokenEntity.builder()
                        .tokenId(tokenId)
                        .build())
                .collect(Collectors.toSet());
        return invalidTokenRepository.saveAll(invalidTokenEntities).then();
    }

    @Override
    public Mono<Void> checkForInvalidityOfToken(String tokenId) {
        return invalidTokenRepository.findByTokenId(tokenId)
                .flatMap(entity -> Mono.error(new TokenAlreadyInvalidatedException(tokenId)))
                .then();
    }
}