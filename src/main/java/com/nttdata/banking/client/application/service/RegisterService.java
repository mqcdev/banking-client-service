package com.nttdata.banking.client.application.service;

import com.nttdata.banking.client.dto.request.RegisterRequest;
import com.nttdata.banking.client.model.Client;
import reactor.core.publisher.Mono;

public interface RegisterService {

    Mono<Client> registerUser(final RegisterRequest registerRequest);

}