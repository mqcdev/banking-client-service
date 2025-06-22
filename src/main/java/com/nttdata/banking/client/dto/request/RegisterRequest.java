package com.nttdata.banking.client.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String names;
    private String surnames;
    private String clientType;
    private String documentType;
    private String documentNumber;
    private Integer cellphone;
    private String email;
    private String userType;
    private String password;
    private Boolean state;
    private String profile;
}