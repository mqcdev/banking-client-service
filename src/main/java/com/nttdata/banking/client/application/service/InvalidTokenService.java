package com.nttdata.banking.client.application.service;

import reactor.core.publisher.Mono;

import java.util.Set;

public interface InvalidTokenService {

    Mono<Void> invalidateTokens(final Set<String> tokenIds);

    Mono<Void> checkForInvalidityOfToken(final String tokenId);

}