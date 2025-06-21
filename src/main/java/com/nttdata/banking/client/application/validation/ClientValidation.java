package com.nttdata.banking.client.application.validation;

import com.nttdata.banking.client.exception.ResourceNotFoundException;
import com.nttdata.banking.client.infrastructure.ClientRepository;
import com.nttdata.banking.client.model.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Class ClientValidation.
 * Client microservice validation class.
 */
@Component
@Slf4j
public class ClientValidation {

    private final ClientRepository clientRepository;

    public ClientValidation(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Validates client type.
     * @param clientType the client type to validate
     * @return Mono<Boolean> true if valid, false otherwise
     */
    public Mono<Boolean> validateClientType(String clientType) {
        log.info("Validating client type: {}", clientType);
        return Mono.just(clientType)
                .map(ct -> "Personal".equals(ct) || "Business".equals(ct));
    }

    /**
     * Validates client profile based on client type.
     * @param client the client to validate
     * @return Mono<Void> empty if valid, error if invalid
     */
    public Mono<Void> validateClientProfile(Client client) {
        log.info("Validating client profile for type: {} and profile: {}",
                client.getClientType(), client.getProfile());

        if (client.getProfile() == null) {
            return Mono.empty();
        }

        return Mono.just(client)
                .flatMap(c -> {
                    if ("Personal".equals(c.getClientType()) && !"VIP".equals(c.getProfile())) {
                        return Mono.error(new ResourceNotFoundException("Perfil", "profile", c.getProfile()));
                    }
                    if ("Business".equals(c.getClientType()) && !"PYME".equals(c.getProfile())) {
                        return Mono.error(new ResourceNotFoundException("Perfil", "profile", c.getProfile()));
                    }
                    return Mono.empty();
                });
    }

    /**
     * Validates that document number is unique.
     * @param documentNumber the document number to validate
     * @param excludeId client ID to exclude from validation (for updates)
     * @return Mono<Void> empty if valid, error if duplicate found
     */
    public Mono<Void> validateUniqueDocumentNumber(String documentNumber, String excludeId) {
        log.info("Validating unique document number: {}", documentNumber);

        return clientRepository.findByDocumentNumber(documentNumber)
                .flatMap(existingClient -> {
                    if (excludeId != null && excludeId.equals(existingClient.getIdClient())) {
                        return Mono.empty(); // Same client, allow update
                    }
                    return Mono.error(new ResourceNotFoundException(
                            "Ya existe un cliente con el número de documento: " + documentNumber));
                })
                .then();
    }

    /**
     * Validates that document number is unique for new clients.
     * @param documentNumber the document number to validate
     * @return Mono<Void> empty if valid, error if duplicate found
     */
    public Mono<Void> validateUniqueDocumentNumber(String documentNumber) {
        return validateUniqueDocumentNumber(documentNumber, null);
    }
}