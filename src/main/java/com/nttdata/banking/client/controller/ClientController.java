package com.nttdata.banking.client.controller;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.nttdata.banking.client.application.service.*;
import com.nttdata.banking.client.dto.SummaryProductsDto;
import com.nttdata.banking.client.infrastructure.InvalidTokenRepository;
import com.nttdata.banking.client.model.Client;
import com.nttdata.banking.client.model.InvalidTokenEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/clients")
@RefreshScope
public class ClientController {

    @Value("${message.demo}")
    private String demoString;

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Client>>> listClients() {
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(service.findAll()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Client>> viewClientDetails(@PathVariable("id") String idClient) {
        log.info("---demoString: " + demoString);
        return service.findById(idClient).map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c));
    }

    @GetMapping("/documentNumber/{documentNumber}")
    public Mono<ResponseEntity<Client>> clientByDocumentNumber(@PathVariable("documentNumber") String documentNumber) {
        return service.clientByDocumentNumber(documentNumber).map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c));
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> saveClient(@Valid @RequestBody Mono<Client> monoClient) {
        Map<String, Object> request = new HashMap<>();
        return monoClient.flatMap(client -> {
            return service.save(client).map(c -> {
                request.put("Cliente", c);
                request.put("mensaje", "Cliente guardado con exito");
                request.put("timestamp", new Date());
                return ResponseEntity.created(URI.create("/api/clients/".concat(c.getIdClient()))).contentType(MediaType.APPLICATION_JSON).body(request);
            });
        });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Client>> editClient(@Valid @RequestBody Client client, @PathVariable("id") String idClient) {
        return service.update(client, idClient).map(c -> ResponseEntity.created(URI.create("/api/clients/".concat(idClient))).contentType(MediaType.APPLICATION_JSON).body(c));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteClient(@PathVariable("id") String idClient) {
        return service.delete(idClient).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
    }

    @PutMapping("/documentNumber/{documentNumber}/profile/{profile}")
    public Mono<ResponseEntity<Client>> updateProfileClient(@PathVariable("documentNumber") String documentNumber, @PathVariable("profile") String profile) {
        return service.updateProfileByDocumentNumber(documentNumber, profile).map(c -> ResponseEntity.created(URI.create("/api/clients/".concat(c.getIdClient()))).contentType(MediaType.APPLICATION_JSON).body(c));
    }

    @GetMapping("/summaryProducts/{documentNumber}")
    public Mono<ResponseEntity<SummaryProductsDto>> getSummaryOfCustomersProducts(@PathVariable String documentNumber) {
        return service.getSummaryOfCustomersProducts(documentNumber)
                .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/find-by-email")
    public Mono<Client> findUserEntityByEmail(@RequestParam String email) {
        return service.findUserEntityByEmail(email);
    }

    @PostMapping("/save")
    public Mono<Client> save(@RequestBody Client client) {
        return service.save(client);
    }

    @PostMapping("/tokens/save-invalid")
    public Mono<Void> saveAllInvalidTokens(@RequestBody Set<InvalidTokenEntity> invalidTokenEntities) {
        return service.saveAllInvalidTokens(invalidTokenEntities);
    }

    @GetMapping("/tokens/find-invalid")
    public Mono<InvalidTokenEntity> findInvalidTokenByTokenId(@RequestParam String tokenId) {
        return service.findInvalidTokenByTokenId(tokenId);
    }

    @GetMapping("/exists-by-email")
    public Mono<Boolean> existsUserEntityByEmail(@RequestParam String email) {
        return service.existsUserEntityByEmail(email);
    }
}
