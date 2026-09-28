package dev.faizarfi.starter.arfiid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;
    private String path;

    public static <T> ApiResponse<T> success(T data,  String message, String path, int status) {
        return new ApiResponse<>(true, LocalDateTime.now(), status, message, data, path);
    }

    public static <T> ApiResponse<T> fail(T data, String message, String path, int status) {
        return new ApiResponse<>(false, LocalDateTime.now(), status, message, data, path);
    }
}

