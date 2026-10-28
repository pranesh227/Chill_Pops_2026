package com.chillpops.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank(message = "Username is required") String username,
            @NotBlank(message = "Password is required") String password
    ) {}

    public record UserInfoResponse(
            Long id,
            String username,
            String role,
            boolean authenticated
    ) {}

    public record LoginResponse(
            boolean success,
            String message,
            UserInfoResponse user
    ) {}
}
