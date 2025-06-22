package com.nttdata.banking.client.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * Represents a request named {@link TokenRefreshRequest} to refresh an access token using a refresh token.
 * This class contains the refresh token required for obtaining a new access token.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshRequest {

    @NotBlank
    private String refreshToken;

}
