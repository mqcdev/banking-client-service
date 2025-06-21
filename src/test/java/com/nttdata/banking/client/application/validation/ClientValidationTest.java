package com.nttdata.banking.client.application.validation;

import com.nttdata.banking.client.exception.ResourceNotFoundException;
import com.nttdata.banking.client.infrastructure.ClientRepository;
import com.nttdata.banking.client.model.Client;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class ClientValidationTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientValidation clientValidation;

    @ParameterizedTest
    @CsvSource({
            "Personal, true",
            "Business, true",
            "Invalid, false",
            "PERSONAL, false",
            "business, false"
    })
    @DisplayName("when validate client type")
    void whenValidateClientType(String clientType, boolean expected) {
        StepVerifier.create(clientValidation.validateClientType(clientType))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    @DisplayName("when validate client profile personal VIP ok")
    void whenValidateClientProfilePersonalVipOk() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);
        client.setProfile("VIP");

        StepVerifier.create(clientValidation.validateClientProfile(client))
                .verifyComplete();
    }

    @Test
    @DisplayName("when validate client profile business PYME ok")
    void whenValidateClientProfileBusinessPymeOk() throws IOException {
        Client client = TestUtil.readFile("client-business", "mocks", Client.class);
        client.setProfile("PYME");

        StepVerifier.create(clientValidation.validateClientProfile(client))
                .verifyComplete();
    }

    @Test
    @DisplayName("when validate client profile personal invalid")
    void whenValidateClientProfilePersonalInvalid() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);
        client.setProfile("INVALID");

        StepVerifier.create(clientValidation.validateClientProfile(client))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("when validate client profile business invalid")
    void whenValidateClientProfileBusinessInvalid() throws IOException {
        Client client = TestUtil.readFile("client-business", "mocks", Client.class);
        client.setProfile("INVALID");

        StepVerifier.create(clientValidation.validateClientProfile(client))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("when validate client profile null ok")
    void whenValidateClientProfileNullOk() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);
        client.setProfile(null);

        StepVerifier.create(clientValidation.validateClientProfile(client))
                .verifyComplete();
    }

    @Test
    @DisplayName("when validate unique document number ok")
    void whenValidateUniqueDocumentNumberOk() {
        Mockito.when(clientRepository.findByDocumentNumber(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(clientValidation.validateUniqueDocumentNumber("12345678"))
                .verifyComplete();
    }

    @Test
    @DisplayName("when validate unique document number duplicate")
    void whenValidateUniqueDocumentNumberDuplicate() throws IOException {
        Client existingClient = TestUtil.readFile("client-ok", "mocks", Client.class);

        Mockito.when(clientRepository.findByDocumentNumber(anyString()))
                .thenReturn(Mono.just(existingClient));

        StepVerifier.create(clientValidation.validateUniqueDocumentNumber("12345678"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("when validate unique document number with exclude id ok")
    void whenValidateUniqueDocumentNumberWithExcludeIdOk() throws IOException {
        Client existingClient = TestUtil.readFile("client-ok", "mocks", Client.class);

        Mockito.when(clientRepository.findByDocumentNumber(anyString()))
                .thenReturn(Mono.just(existingClient));

        StepVerifier.create(clientValidation.validateUniqueDocumentNumber("12345678", "1"))
                .verifyComplete();
    }

    @Test
    @DisplayName("when validate unique document number with different exclude id")
    void whenValidateUniqueDocumentNumberWithDifferentExcludeId() throws IOException {
        Client existingClient = TestUtil.readFile("client-ok", "mocks", Client.class);

        Mockito.when(clientRepository.findByDocumentNumber(anyString()))
                .thenReturn(Mono.just(existingClient));

        StepVerifier.create(clientValidation.validateUniqueDocumentNumber("12345678", "2"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}