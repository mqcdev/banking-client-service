package com.nttdata.banking.client.application.service;

import com.nttdata.banking.client.dto.request.TokenInvalidateRequest;
import reactor.core.publisher.Mono;

public interface LogoutService {

    Mono<Void> logout(final TokenInvalidateRequest tokenInvalidateRequest);

}