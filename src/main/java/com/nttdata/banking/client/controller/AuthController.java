package com.nttdata.banking.client.controller;

import com.nttdata.banking.client.application.service.*;
import com.nttdata.banking.client.dto.request.LoginRequest;
import com.nttdata.banking.client.dto.request.RegisterRequest;
import com.nttdata.banking.client.dto.request.TokenInvalidateRequest;
import com.nttdata.banking.client.dto.request.TokenRefreshRequest;
import com.nttdata.banking.client.dto.response.CustomResponse;
import com.nttdata.banking.client.dto.response.TokenResponse;
import com.nttdata.banking.client.model.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterService registerService;
    private final TokenService tokenService;
    private final UserLoginService userLoginService;
    private final RefreshTokenService refreshTokenService;
    private final LogoutService logoutService;

    @PostMapping("/register")
    public Mono<CustomResponse<Void>> registerUser(@RequestBody @Validated final RegisterRequest registerRequest) {
        log.info("ClientController | registerUser");
        return registerService.registerUser(registerRequest)
                .thenReturn(CustomResponse.SUCCESS);
    }

    @PostMapping("/validate-token")
    public Mono<ResponseEntity<Void>> validateToken(@RequestParam String token) {
        log.info("ClientController | validateToken");
        return tokenService.verifyAndValidate(token)
                .thenReturn(ResponseEntity.ok().build());
    }

    private TokenResponse toTokenResponse(Token token) {
        return TokenResponse.builder()
                .accessToken(token.getAccessToken())
                .accessTokenExpiresAt(token.getAccessTokenExpiresAt())
                .refreshToken(token.getRefreshToken())
                .build();
    }

    @PostMapping("/login")
    public Mono<CustomResponse<TokenResponse>> loginUser(@RequestBody @Validated final LoginRequest loginRequest) {
        log.info("ClientController | loginUser");
        return userLoginService.login(loginRequest)
                .map(this::toTokenResponse)
                .map(CustomResponse::successOf);
    }

    @PostMapping("/refresh-token")
    public Mono<CustomResponse<TokenResponse>> refreshToken(@RequestBody @Validated final TokenRefreshRequest tokenRefreshRequest) {
        log.info("ClientController | refreshToken");
        return refreshTokenService.refreshToken(tokenRefreshRequest)
                .map(this::toTokenResponse)
                .map(CustomResponse::successOf);
    }

    @PostMapping("/logout")
    public Mono<CustomResponse<Void>> logout(@RequestBody @Validated final TokenInvalidateRequest tokenInvalidateRequest) {
        log.info("ClientController | logout");
        return logoutService.logout(tokenInvalidateRequest)
                .thenReturn(CustomResponse.SUCCESS);
    }

    @GetMapping("/authenticate")
    public Mono<ResponseEntity<UsernamePasswordAuthenticationToken>> getAuthentication(@RequestParam String token) {
        return tokenService.getAuthentication(token)
                .map(ResponseEntity::ok);
    }
}
