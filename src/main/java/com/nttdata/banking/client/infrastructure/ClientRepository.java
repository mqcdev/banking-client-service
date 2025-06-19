package com.nttdata.banking.client.infrastructure;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.banking.client.model.Client;
import reactor.core.publisher.Mono;

/**
 * Class ClientRepository.
 * Client microservice class ClientRepository.
 */
public interface ClientRepository extends ReactiveMongoRepository<Client, String> {

    public Mono<Client> findByDocumentNumber(String documentNumber);

}
