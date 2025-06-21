package com.nttdata.banking.client.infrastructure;

import com.nttdata.banking.client.config.WebClientConfig;
import com.nttdata.banking.client.model.Credit;
import com.nttdata.banking.client.model.Loan;
import com.nttdata.banking.client.util.Constants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class LoanRepository {

    @Value("${local.property.host.ms-loan}")
    private String propertyHostMsLoan;

    @CircuitBreaker(name = Constants.LOAN_CB, fallbackMethod = "getDefaultLoanByDocumentNumber")
    public Flux<Loan> findLoanByDocumentNumber(String documentNumber) {

        log.info("Inicio----findLoanByDocumentNumber-------: ");
        WebClientConfig webconfig = new WebClientConfig();
        Flux<Loan> alerts = webconfig.setUriData("http://" + propertyHostMsLoan + ":8092")
                .flatMap(d -> webconfig.getWebclient().get()
                        .uri("/api/loans/loansDetails/" + documentNumber).retrieve()
                        .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new Exception("Error 400")))
                        .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new Exception("Error 500")))
                        .bodyToFlux(Loan.class)
                        .collectList()
                )
                .flatMapMany(iterable -> Flux.fromIterable(iterable));
        return alerts;
    }
}