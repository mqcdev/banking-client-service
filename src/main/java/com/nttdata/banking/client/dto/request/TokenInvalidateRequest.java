package com.nttdata.banking.client.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * Represents a request named {@link TokenInvalidateRequest} to invalidate tokens.
 * This class contains the access and refresh tokens that need to be invalidated.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInvalidateRequest {

    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;

}
