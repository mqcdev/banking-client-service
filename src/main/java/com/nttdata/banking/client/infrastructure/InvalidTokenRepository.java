package com.nttdata.banking.client.infrastructure;


import com.nttdata.banking.client.model.InvalidTokenEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repository interface for managing {@link InvalidTokenEntity} instances.
 * Provides CRUD operations and additional query methods for {@link InvalidTokenEntity}.
 */
public interface InvalidTokenRepository extends ReactiveMongoRepository<InvalidTokenEntity, String> {

    Mono<InvalidTokenEntity> findByTokenId(final String tokenId);

}
