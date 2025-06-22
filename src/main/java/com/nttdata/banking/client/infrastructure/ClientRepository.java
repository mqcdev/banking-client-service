package com.nttdata.banking.client.infrastructure;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.banking.client.model.Client;
import reactor.core.publisher.Mono;

/**
 * Repository interface for Client entity.
 * Provides reactive database operations for Client.
 */
public interface ClientRepository extends ReactiveMongoRepository<Client, String> {

    /**
     * Finds a client by document number.
     *
     * @param documentNumber the document number to search for
     * @return Mono<Client> the client if found, empty if not found
     */
    Mono<Client> findByDocumentNumber(String documentNumber);

    /**
     * Finds a client by email address.
     *
     * @param email the email address to search for
     * @return Mono<Client> the client if found, empty if not found
     */
    Mono<Client> findByEmail(String email);

    Mono<Boolean> existsUserEntityByEmail(final String email);
    Mono<Client> findUserEntityByEmail(final String email);


}

