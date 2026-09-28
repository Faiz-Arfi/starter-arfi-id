package dev.faizarfi.starter.arfiid.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExchangeCodeRequest {
    @NotBlank
    private String grantType;
    @NotBlank
    private String code;
    @NotBlank
    private String redirectUri;
}
