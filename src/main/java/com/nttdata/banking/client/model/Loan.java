package com.nttdata.banking.client.model;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class Loan.
 * Client microservice class Loan.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Loan {

    @Id
    private String idLoan;
    private Client client;
    private Integer loanNumber;
    private String loanType;
    private Double loanAmount;
    private String currency;
    private Integer numberQuotas;
    private String status;
    private Double debtBalance;
}
