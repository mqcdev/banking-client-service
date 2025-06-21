package com.nttdata.banking.client.controller;

import com.nttdata.banking.client.application.service.ClientService;
import com.nttdata.banking.client.dto.SummaryProductsDto;
import com.nttdata.banking.client.exception.ResourceNotFoundException;
import com.nttdata.banking.client.model.Client;
import com.nttdata.banking.client.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    @Test
    @DisplayName("when list clients ok")
    void whenListClientsOk() throws IOException {
        Client client1 = TestUtil.readFile("client-ok", "mocks", Client.class);
        Client client2 = TestUtil.readFile("client-business", "mocks", Client.class);

        Mockito.when(clientService.findAll())
                .thenReturn(Flux.just(client1, client2));

        StepVerifier.create(clientController.listClients())
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when view client details ok")
    void whenViewClientDetailsOk() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);
        ReflectionTestUtils.setField(clientController, "demoString", "test message");

        Mockito.when(clientService.findById(anyString()))
                .thenReturn(Mono.just(client));

        StepVerifier.create(clientController.viewClientDetails("1"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals("John", response.getBody().getNames());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when view client details not found")
    void whenViewClientDetailsNotFound() {
        Mockito.when(clientService.findById(anyString()))
                .thenReturn(Mono.error(new ResourceNotFoundException("Cliente", "Id", "999")));

        StepVerifier.create(clientController.viewClientDetails("999"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("when client by document number ok")
    void whenClientByDocumentNumberOk() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);

        Mockito.when(clientService.clientByDocumentNumber(anyString()))
                .thenReturn(Mono.just(client));

        StepVerifier.create(clientController.clientByDocumentNumber("12345678"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals("12345678", response.getBody().getDocumentNumber());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when save client ok")
    void whenSaveClientOk() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);

        Mockito.when(clientService.save(any(Client.class)))
                .thenReturn(Mono.just(client));

        StepVerifier.create(clientController.saveClient(Mono.just(client)))
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when save client with error")
    void whenSaveClientWithError() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);

        Mockito.when(clientService.save(any(Client.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException("Tipo Cliente", "ClientType", "Invalid")));

        StepVerifier.create(clientController.saveClient(Mono.just(client)))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("when edit client ok")
    void whenEditClientOk() throws IOException {
        Client client = TestUtil.readFile("client-updated", "mocks", Client.class);

        Mockito.when(clientService.update(any(Client.class), anyString()))
                .thenReturn(Mono.just(client));

        StepVerifier.create(clientController.editClient(client, "1"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    assertEquals("Jane", response.getBody().getNames());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when delete client ok")
    void whenDeleteClientOk() {
        Mockito.when(clientService.delete(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(clientController.deleteClient("1"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when update profile client ok")
    void whenUpdateProfileClientOk() throws IOException {
        Client client = TestUtil.readFile("client-ok", "mocks", Client.class);
        client.setProfile("VIP");

        Mockito.when(clientService.updateProfileByDocumentNumber(anyString(), anyString()))
                .thenReturn(Mono.just(client));

        StepVerifier.create(clientController.updateProfileClient("12345678", "VIP"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    assertEquals("VIP", response.getBody().getProfile());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when get summary of customers products ok")
    void whenGetSummaryOfCustomersProductsOk() throws IOException {
        SummaryProductsDto summary = TestUtil.readFile("summary-ok", "mocks", SummaryProductsDto.class);

        Mockito.when(clientService.getSummaryOfCustomersProducts(anyString()))
                .thenReturn(Mono.just(summary));

        StepVerifier.create(clientController.getSummaryOfCustomersProducts("12345678"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals("12345678", response.getBody().getDocumentNumber());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when get summary of customers products not found")
    void whenGetSummaryOfCustomersProductsNotFound() {
        Mockito.when(clientService.getSummaryOfCustomersProducts(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(clientController.getSummaryOfCustomersProducts("99999999"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                })
                .verifyComplete();
    }
}