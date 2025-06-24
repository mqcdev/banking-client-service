package com.nttdata.banking.client.application.service.impl;

import com.nttdata.banking.client.application.service.ClientService;
import com.nttdata.banking.client.application.validation.ClientValidation;
import com.nttdata.banking.client.dto.SummaryProductsDto;
import com.nttdata.banking.client.exception.ResourceNotFoundException;
import com.nttdata.banking.client.infrastructure.ClientRepository;
import com.nttdata.banking.client.infrastructure.CreditRepository;
import com.nttdata.banking.client.infrastructure.InvalidTokenRepository;
import com.nttdata.banking.client.infrastructure.LoanRepository;
import com.nttdata.banking.client.model.Client;
import com.nttdata.banking.client.model.InvalidTokenEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private ClientValidation clientValidation;

    @Autowired
    private InvalidTokenRepository invalidTokenRepository;

    @Override
    public Flux<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Mono<Client> findById(String idClient) {
        return Mono.just(idClient)
                .flatMap(clientRepository::findById)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente", "Id", idClient)));
    }

    @Override
    public Mono<Client> save(Client client) {
        // Verificar tipo de cliente
        return clientValidation.validateClientType(client.getClientType())
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.error(new ResourceNotFoundException("Tipo Cliente", "ClientType", client.getClientType()));
                    }
                    // Verificar documento único
                    return clientValidation.validateUniqueDocumentNumber(client.getDocumentNumber())
                            .onErrorResume(error -> Mono.error(error)) // Propagar errores
                            .then(Mono.defer(() -> {
                                // Verificar perfil de cliente
                                return clientValidation.validateClientProfile(client)
                                        .onErrorResume(error -> Mono.error(error)) // Propagar errores
                                        .then(Mono.defer(() -> clientRepository.save(client)));
                            }));
                });
    }

    @Override
    public Mono<Client> update(Client client, String idClient) {
        return clientValidation.validateClientType(client.getClientType())
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.error(new ResourceNotFoundException("Tipo Cliente", "ClientType", client.getClientType()));
                    }
                    return clientRepository.findById(idClient)
                            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente", "Id", idClient)))
                            .flatMap(existingClient -> {
                                // Actualizar campos
                                existingClient.setNames(client.getNames());
                                existingClient.setSurnames(client.getSurnames());
                                existingClient.setClientType(client.getClientType());
                                existingClient.setDocumentType(client.getDocumentType());
                                existingClient.setDocumentNumber(client.getDocumentNumber());
                                existingClient.setCellphone(client.getCellphone());
                                existingClient.setEmail(client.getEmail());
                                existingClient.setState(client.getState());
                                existingClient.setProfile(client.getProfile());

                                // Validar documento único para updates
                                return clientValidation.validateUniqueDocumentNumber(client.getDocumentNumber(), idClient)
                                        .onErrorResume(error -> Mono.error(error))
                                        .then(Mono.defer(() -> {
                                            // Validar perfil
                                            return clientValidation.validateClientProfile(existingClient)
                                                    .onErrorResume(error -> Mono.error(error))
                                                    .then(Mono.defer(() -> clientRepository.save(existingClient)));
                                        }));
                            });
                });
    }

    @Override
    public Mono<Void> delete(String idClient) {
        return clientRepository.findById(idClient)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente", "Id", idClient)))
                .flatMap(clientRepository::delete);
    }

    @Override
    public Mono<Client> clientByDocumentNumber(String documentNumber) {
        return Mono.just(documentNumber)
                .flatMap(clientRepository::findByDocumentNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente", "documentNumber", documentNumber)));
    }

    @Override
    public Mono<Client> updateProfileByDocumentNumber(String documentNumber, String profile) {
        return clientRepository.findByDocumentNumber(documentNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente", "documentNumber", documentNumber)))
                .flatMap(client -> {
                    log.info("Updating profile for document: {} to profile: {}", documentNumber, profile);
                    client.setProfile("0".equals(profile) ? null : profile);
                    return clientValidation.validateClientProfile(client)
                            .onErrorResume(error -> Mono.error(error))
                            .then(Mono.defer(() -> update(client, client.getIdClient())));
                });
    }

    @Override
    public Mono<SummaryProductsDto> getSummaryOfCustomersProducts(String documentNumber) {
        SummaryProductsDto mapperDtoCredit = new SummaryProductsDto();

        return creditRepository.findCreditsByDocumentNumber(documentNumber)
                .collectList()
                .flatMap(credits ->
                        loanRepository.findLoanByDocumentNumber(documentNumber)
                                .collectList()
                                .flatMap(loans ->
                                        mapperDtoCredit.mapperToSummaryProductsDtoOfCredit(credits, loans, documentNumber)
                                )
                );
    }

    @Override
    public Mono<Boolean> existsUserEntityByEmail(final String email) {
        return clientRepository.existsUserEntityByEmail(email);
    }

    @Override
    public Mono<Client> findUserEntityByEmail(final String email) {
        return clientRepository.findUserEntityByEmail(email);
    }

    @Override
    public Mono<Void> saveAllInvalidTokens(Set<InvalidTokenEntity> invalidTokenEntities) {
        return invalidTokenRepository.saveAll(invalidTokenEntities).then();
    }

    @Override
    public Mono<InvalidTokenEntity> findInvalidTokenByTokenId(String tokenId) {
        return invalidTokenRepository.findByTokenId(tokenId);
    }
}