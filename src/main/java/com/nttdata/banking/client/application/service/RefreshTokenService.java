package com.nttdata.banking.client.application.service;

import com.nttdata.banking.client.dto.request.TokenRefreshRequest;
import com.nttdata.banking.client.model.Token;
import reactor.core.publisher.Mono;

public interface RefreshTokenService {

    Mono<Token> refreshToken(final TokenRefreshRequest tokenRefreshRequest);

}
