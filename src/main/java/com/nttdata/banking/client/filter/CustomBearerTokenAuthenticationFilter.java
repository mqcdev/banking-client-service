package com.nttdata.banking.client.filter;

import com.nttdata.banking.client.application.service.InvalidTokenService;
import com.nttdata.banking.client.application.service.TokenService;
import com.nttdata.banking.client.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomBearerTokenAuthenticationFilter implements WebFilter {

    private final TokenService tokenService;
    private final InvalidTokenService invalidTokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (TokenUtils.isBearerToken(authorizationHeader)) {
            String jwt = TokenUtils.getJwt(authorizationHeader);

            return tokenService.verifyAndValidate(jwt)
                    .then(tokenService.getId(jwt))
                    .flatMap(tokenId -> invalidTokenService.checkForInvalidityOfToken(tokenId).thenReturn(tokenId))
                    .flatMap(tokenId -> tokenService.getAuthentication(jwt))
                    .flatMap(authentication -> chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                    );
        }

        return chain.filter(exchange);
    }
}