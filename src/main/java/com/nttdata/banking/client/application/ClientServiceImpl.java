package com.nttdata.banking.client.application;

import com.nttdata.banking.client.dto.SummaryProductsDto;
import com.nttdata.banking.client.exception.ResourceNotFoundException;
import com.nttdata.banking.client.infrastructure.ClientRepository;
import com.nttdata.banking.client.infrastructure.CreditRepository;
import com.nttdata.banking.client.infrastructure.LoanRepository;
import com.nttdata.banking.client.model.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CreditRepository creditRepository;

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
        return client.validateClientType(client.getClientType())
                .flatMap(c -> {
                    if (c.equals(true)) {
                        return clientRepository.save(client);
                    } else {
                        return Mono.error(new ResourceNotFoundException("Tipo Cliente", "ClientType", client.getClientType()));
                    }
                });
    }

    @Override
    public Mono<Client> update(Client client, String idClient) {
        return client.validateClientType(client.getClientType())
                .flatMap(ct -> {
                    if (ct.equals(true)) {
                        return clientRepository.findById(idClient)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente", "Id", idClient)))
                                .flatMap(c -> {
                                    c.setNames(client.getNames());
                                    c.setSurnames(client.getSurnames());
                                    c.setClientType(client.getClientType());
                                    c.setDocumentType(client.getDocumentType());
                                    c.setDocumentNumber(client.getDocumentNumber());
                                    c.setCellphone(client.getCellphone());
                                    c.setEmail(client.getEmail());
                                    c.setState(client.getState());
                                    c.setProfile(client.getProfile());
                                    return clientRepository.save(c);
                                });
                    } else {
                        return Mono.error(new ResourceNotFoundException("Tipo Cliente", "ClientType", client.getClientType()));
                    }
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
        return Mono.just(documentNumber)
                .flatMap(clientRepository::findByDocumentNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente", "documentNumber", documentNumber)))
                .flatMap(c -> {
                    log.info("profile-------: " + profile);
                    c.setProfile(profile.equals("0") ? null : profile);
                    return Mono.just(c);
                })
                .flatMap(c -> c.validateClientProfile().then(Mono.just(c)))
                .flatMap(c -> update(c, c.getIdClient()));
    }

    @Override
    public Mono<SummaryProductsDto> getSummaryOfCustomersProducts(String documentNumber) {

        SummaryProductsDto mapperDtoCredit = new SummaryProductsDto();

        return creditRepository.findCreditsByDocumentNumber(documentNumber)
                .collectList()
                .flatMap(c -> {
                    return loanRepository.findLoanByDocumentNumber(documentNumber)
                            .collectList()
                            .flatMap(l -> {
                                return mapperDtoCredit.mapperToSummaryProductsDtoOfCredit(c, l, documentNumber);
                            });
                });
    }

}