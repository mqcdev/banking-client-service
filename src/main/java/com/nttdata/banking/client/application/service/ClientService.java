package com.nttdata.banking.client.application.service;

import com.nttdata.banking.client.dto.SummaryProductsDto;
import com.nttdata.banking.client.model.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

}
