package com.nttdata.banking.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtDecoderConfig {

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(TokenConfigurationParameter tokenConfig) {
        RSAPublicKey publicKey = (RSAPublicKey) tokenConfig.getPublicKey();
        return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
    }
}