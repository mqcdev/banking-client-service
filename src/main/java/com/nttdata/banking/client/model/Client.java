package com.nttdata.banking.client.model;

import com.nttdata.banking.client.exception.ResourceNotFoundException;
import lombok.Builder;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;
import javax.validation.constraints.*;

/**
 * Class Client Model.
 * Client microservice class Client.
 */
@Document(collection = "Client")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Slf4j
public class Client {

    @Id
    private String idClient;

    @NotEmpty(message = "no debe estar vacío")
    private String names;

    @NotEmpty(message = "no debe estar vacío")
    private String surnames;

    @NotEmpty(message = "no debe estar vacío")
    private String clientType;

    @NotEmpty(message = "no debe estar vacío")
    private String documentType;

    @NotEmpty(message = "no debe estar vacío")
    private String documentNumber;

    @Max(value = 999999999, message = "no debe tener más de 9 cifras")
    private Integer cellphone;

    @Email(message = "debe tener formato de correo")
    private String email;

    private Boolean state;

    private String profile;

    public Mono<Boolean> validateClientType(String clientType) {
        log.info("ini----validateClientType-------: ");
        return Mono.just(clientType).flatMap(ct -> {
            Boolean isOk = false;
            if (clientType.equals("Personal")) {
                isOk = true;
            }
            if (clientType.equals("Business")) {
                isOk = true;
            }
            return Mono.just(isOk);
        });
    }

    public Mono<Void> validateClientProfile() {
        log.info("ini----validateClientProfile-------: ");
        return Mono.just(this.clientType).flatMap(ct -> {
            if (this.profile != null) {
                log.info("0----validateClientProfile-------this.profile: " + this.profile);
                if (this.clientType.equals("Personal")) {
                    if (!this.profile.equals("VIP")) {
                        log.info("1----validateClientProfile-------: ");
                        return Mono.error(new ResourceNotFoundException("Perfil", "profile", this.profile));
                    }
                }
                if (this.clientType.equals("Business")) {
                    if (!this.profile.equals("PYME")) {
                        log.info("2----validateClientProfile-------: ");
                        return Mono.error(new ResourceNotFoundException("Perfil", "profile", this.profile));
                    }
                }
            }
            log.info("4----validateClientProfile-------: ");
            return Mono.empty();
        });
    }
}