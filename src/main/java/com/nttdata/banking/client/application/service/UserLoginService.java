package com.nttdata.banking.client.application.service;


import com.nttdata.banking.client.dto.request.LoginRequest;
import com.nttdata.banking.client.model.Token;
import reactor.core.publisher.Mono;


public interface UserLoginService {

    Mono<Token> login(final LoginRequest loginRequest);

}
