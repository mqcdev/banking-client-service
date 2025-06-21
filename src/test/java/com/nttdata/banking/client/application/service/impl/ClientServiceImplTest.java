package com.nttdata.banking.client.application.service.impl;

import com.nttdata.banking.client.application.validation.ClientValidation;
import com.nttdata.banking.client.dto.SummaryProductsDto;
import com.nttdata.banking.client.exception.ResourceNotFoundException;
import com.nttdata.banking.client.infrastructure.ClientRepository;
import com.nttdata.banking.client.infrastructure.CreditRepository;
import com.nttdata.banking.client.infrastructure.LoanRepository;
import com.nttdata.banking.client.model.Client;
import com.nttdata.banking.client.model.Credit;
import com.nttdata.banking.client.model.Loan;
import com.nttdata.banking.client.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private ClientValidation clientValidation;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    @DisplayName("when save client ok")
    void whenSaveClientOk() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);

        Mockito.when(clientValidation.validateClientType("Personal"))
                .thenReturn(Mono.just(true));
        Mockito.when(clientValidation.validateUniqueDocumentNumber("12345678"))
                .thenReturn(Mono.empty());
        Mockito.when(clientValidation.validateClientProfile(any(Client.class)))
                .thenReturn(Mono.empty());
        Mockito.when(clientRepository.save(any(Client.class)))
                .thenReturn(Mono.just(client));

        StepVerifier.create(clientService.save(client))
                .expectNext(client)
                .verifyComplete();
    }

    @Test
    @DisplayName("when save client with invalid type")
    void whenSaveClientWithInvalidType() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);
        client.setClientType("Invalid");

        Mockito.when(clientValidation.validateClientType("Invalid"))
                .thenReturn(Mono.just(false));

        StepVerifier.create(clientService.save(client))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("when save client with duplicate document number")
    void whenSaveClientWithDuplicateDocumentNumber() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);

        // Mock sequence for the new flow structure
        Mockito.when(clientValidation.validateClientType("Personal"))
                .thenReturn(Mono.just(true));
        Mockito.when(clientValidation.validateUniqueDocumentNumber("12345678"))
                .thenReturn(Mono.error(new ResourceNotFoundException("Ya existe un cliente con el número de documento: " + client.getDocumentNumber())));

        StepVerifier.create(clientService.save(client))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("when get list clients ok")
    void whenGetListClientsOk() throws IOException {
        Client client1 = TestUtil.readFile("client-ok", "mocks", Client.class);
        Client client2 = TestUtil.readFile("client-business", "mocks", Client.class);

        Mockito.when(clientRepository.findAll())
                .thenReturn(Flux.just(client1, client2));

        StepVerifier.create(clientService.findAll().map(Client::getNames))
                .expectNext("John")
                .thenAwait(Duration.ofMillis(100))
                .expectNext("Company ABC")
                .expectComplete()
                .verify();
    }

    @ParameterizedTest
    @CsvSource({
            "1",
            "2"
    })
    @DisplayName("when get client by id ok")
    void whenGetClientByIdOk(String id) throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);
        client.setIdClient(id);

        Mockito.when(clientRepository.findById(id))
                .thenReturn(Mono.just(client));

        StepVerifier.create(clientService.findById(id))
                .assertNext(c -> assertEquals(c.getNames(), "John"))
                .verifyComplete();
    }

    @Test
    @DisplayName("when get client by id not found")
    void whenGetClientByIdNotFound() {
        Mockito.when(clientRepository.findById("999"))
                .thenReturn(Mono.empty());

        StepVerifier.create(clientService.findById("999"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("when delete client ok")
    void whenDeleteClientOk() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);

        Mockito.when(clientRepository.findById("1"))
                .thenReturn(Mono.just(client));
        Mockito.when(clientRepository.delete(client))
                .thenReturn(Mono.empty());

        StepVerifier.create(clientService.delete("1"))
                .verifyComplete();
    }

    @Test
    @DisplayName("when delete client not found")
    void whenDeleteClientNotFound() {
        Mockito.when(clientRepository.findById("999"))
                .thenReturn(Mono.empty());

        StepVerifier.create(clientService.delete("999"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("when update client ok")
    void whenUpdateClientOk() throws IOException {
        Client existingClient = TestUtil.readFile("client-ok", "mocks", Client.class);
        Client updatedClient = TestUtil.readFile("client-updated", "mocks", Client.class);

        Mockito.when(clientValidation.validateClientType("Personal"))
                .thenReturn(Mono.just(true));
        Mockito.when(clientRepository.findById("1"))
                .thenReturn(Mono.just(existingClient));
        Mockito.when(clientValidation.validateUniqueDocumentNumber("87654321", "1"))
                .thenReturn(Mono.empty());
        Mockito.when(clientValidation.validateClientProfile(any(Client.class)))
                .thenReturn(Mono.empty());
        Mockito.when(clientRepository.save(any(Client.class)))
                .thenReturn(Mono.just(updatedClient));

        StepVerifier.create(clientService.update(updatedClient, "1"))
                .assertNext(client -> {
                    assertEquals(client.getNames(), "Jane");
                    assertEquals(client.getSurnames(), "Smith");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when get client by document number ok")
    void whenGetClientByDocumentNumberOk() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);

        Mockito.when(clientRepository.findByDocumentNumber("12345678"))
                .thenReturn(Mono.just(client));

        StepVerifier.create(clientService.clientByDocumentNumber("12345678"))
                .assertNext(c -> assertEquals(c.getDocumentNumber(), "12345678"))
                .verifyComplete();
    }

    @Test
    @DisplayName("when update profile by document number ok")
    void whenUpdateProfileByDocumentNumberOk() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);
        Client updatedClient = TestUtil.readFile("client-ok", "mocks", Client.class);
        updatedClient.setProfile("VIP");

        // Mock for findByDocumentNumber
        Mockito.when(clientRepository.findByDocumentNumber("12345678"))
                .thenReturn(Mono.just(client));

        // Mock for validateClientProfile
        Mockito.when(clientValidation.validateClientProfile(any(Client.class)))
                .thenReturn(Mono.empty());

        // Mock for update method dependencies
        Mockito.when(clientValidation.validateClientType("Personal"))
                .thenReturn(Mono.just(true));
        Mockito.when(clientValidation.validateUniqueDocumentNumber("12345678", "1"))
                .thenReturn(Mono.empty());
        Mockito.when(clientRepository.findById("1"))
                .thenReturn(Mono.just(client));
        Mockito.when(clientRepository.save(any(Client.class)))
                .thenReturn(Mono.just(updatedClient));

        StepVerifier.create(clientService.updateProfileByDocumentNumber("12345678", "VIP"))
                .assertNext(c -> assertEquals(c.getProfile(), "VIP"))
                .verifyComplete();
    }

    @Test
    @DisplayName("when get summary of customer products ok")
    void whenGetSummaryOfCustomerProductsOk() throws IOException {
        Credit credit = TestUtil.readFile("credit-ok", "mocks", Credit.class);
        Loan loan = TestUtil.readFile("loan-ok", "mocks", Loan.class);

        Mockito.when(creditRepository.findCreditsByDocumentNumber("12345678"))
                .thenReturn(Flux.just(credit));
        Mockito.when(loanRepository.findLoanByDocumentNumber("12345678"))
                .thenReturn(Flux.just(loan));

        StepVerifier.create(clientService.getSummaryOfCustomersProducts("12345678"))
                .assertNext(s -> assertEquals(s.getDocumentNumber(), "12345678"))
                .verifyComplete();
    }
}