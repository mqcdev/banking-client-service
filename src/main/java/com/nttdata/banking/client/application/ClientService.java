package com.nttdata.banking.client.application;

import com.nttdata.banking.client.dto.SummaryProductsDto;
import com.nttdata.banking.client.model.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class ClientService.
 * Client microservice class ClientService.
 */
public interface ClientService {

    public Flux<Client> findAll();

    public Mono<Client> findById(String idClient);

    public Mono<Client> save(Client client);

    public Mono<Client> update(Client client, String idClient);

    public Mono<Void> delete(String idClient);

    public Mono<Client> clientByDocumentNumber(String documentNumber);

    public Mono<Client> updateProfileByDocumentNumber(String documentNumber, String profile);

    public Mono<SummaryProductsDto> getSummaryOfCustomersProducts(String documentNumber);

}
