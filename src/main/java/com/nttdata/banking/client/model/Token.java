package com.nttdata.banking.client.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Token {
    private String accessToken;
    private long accessTokenExpiresAt;
    private String refreshToken;
    private long refreshTokenExpiresAt;

    /**
     * Verifica si el encabezado contiene un token Bearer.
     * @param authHeader El encabezado de autorización
     * @return true si es un token Bearer válido
     */
    public static boolean isBearerToken(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    /**
     * Extrae el token JWT de un encabezado de autorización.
     * @param authHeader El encabezado de autorización
     * @return El token JWT sin el prefijo "Bearer "
     */
    public static String getJwt(String authHeader) {
        if (isBearerToken(authHeader)) {
            return authHeader.substring(7);
        }
        return null;
    }
}