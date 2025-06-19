package com.nttdata.banking.client.dto;

import java.util.List;
import com.nttdata.banking.client.model.Credit;
import com.nttdata.banking.client.model.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


/**
 * Class SummaryProductsDto.
 * Client microservice class SummaryProductsDto.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
public class SummaryProductsDto {

    private String documentNumber;
    private List<Loan> loan;
    private List<Credit> creditCard;

    public Mono<SummaryProductsDto> mapperToSummaryProductsDtoOfCredit
            (List<Credit> listCredit, List<Loan> listLoan, String documentNumber) {
        log.info("Inicio mapperToSummaryProductsDtoOfCredit-------: ");
        SummaryProductsDto sumProductDto = SummaryProductsDto.builder()
                .documentNumber(documentNumber)
                .creditCard(listCredit)
                .loan(listLoan).build();
        log.info("Fin mapperToSummaryProductsDtoOfCredit-------: ");
        return Mono.just(sumProductDto);
    }

}
