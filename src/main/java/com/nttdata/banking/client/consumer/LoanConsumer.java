package com.nttdata.banking.client.consumer;

import com.nttdata.banking.client.infrastructure.LoanRepository;
import com.nttdata.banking.client.model.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoanConsumer {

    private final LoanRepository loanRepository;

    @KafkaListener(topics = "${spring.kafka.topic.loan.name}")
    public void listener(@Payload Loan loan) {
        log.debug("Message received : {} ", loan);
        applyListLoans(loan).blockFirst();
    }

    private Flux<Loan> applyListLoans(Loan request) {
        log.debug("applyListLoans executed : {} ", request);
        return loanRepository.findLoanByDocumentNumber(
                request.getClient().getDocumentNumber());
    }
}
