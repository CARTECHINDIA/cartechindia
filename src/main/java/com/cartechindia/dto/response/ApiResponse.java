package com.cartechindia.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Generic API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "HTTP status code of the response", example = "200")
    private int status;

    @Schema(description = "Human-readable message", example = "Operation successful")
    private String message;

    @Schema(description = "Actual data returned by the API")
    private T data;
}
