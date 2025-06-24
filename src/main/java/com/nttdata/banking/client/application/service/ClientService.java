package com.nttdata.banking.client.application.service;

import com.nttdata.banking.client.dto.SummaryProductsDto;
import com.nttdata.banking.client.model.Client;
import com.nttdata.banking.client.model.InvalidTokenEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Class ClientService.
 * Client microservice class ClientService.
 */
public interface ClientService {

    Flux<Client> findAll();

    Mono<Client> findById(String idClient);

    Mono<Client> save(Client client);

    Mono<Client> update(Client client, String idClient);

    Mono<Void> delete(String idClient);

    Mono<Client> clientByDocumentNumber(String documentNumber);

    Mono<Client> updateProfileByDocumentNumber(String documentNumber, String profile);

    Mono<SummaryProductsDto> getSummaryOfCustomersProducts(String documentNumber);

    Mono<Boolean> existsUserEntityByEmail(final String email);

    Mono<Client> findUserEntityByEmail(final String email);

    Mono<Void> saveAllInvalidTokens(Set<InvalidTokenEntity> invalidTokenEntities);

    Mono<InvalidTokenEntity> findInvalidTokenByTokenId(String tokenId);
}
