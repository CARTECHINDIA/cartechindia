package com.cartechindia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(description = "Paginated response")
public class PageResponse<T> {

    // getters and setters
    @Schema(description = "Current page content")
    private List<T> content;

    @Schema(description = "Current page number (0-based)")
    private int number;

    @Schema(description = "Size of the page")
    private int size;

    @Schema(description = "Total number of elements")
    private long totalElements;

    @Schema(description = "Total number of pages")
    private int totalPages;

    @Schema(description = "Is this the first page?")
    private boolean first;

    @Schema(description = "Is this the last page?")
    private boolean last;

}
