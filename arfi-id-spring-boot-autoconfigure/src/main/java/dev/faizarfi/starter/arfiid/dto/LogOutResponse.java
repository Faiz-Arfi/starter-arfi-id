package dev.faizarfi.starter.arfiid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogOutResponse {
    private String status;
    private String message;
}
