package com.cartechindia.controller;

import com.cartechindia.dto.BiddingDto;
import com.cartechindia.dto.BiddingResponseDto;
import com.cartechindia.dto.PageResponse;
import com.cartechindia.entity.Bidding;
import com.cartechindia.service.BiddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bidding")
@RequiredArgsConstructor
@Tag(name = "Bidding API", description = "Endpoints for managing biddings")
public class BiddingController {

    private final BiddingService biddingService;

    @PostMapping
    @PreAuthorize("hasRole('DEALER')")
    @Operation(
            summary = "Create a new bidding",
            description = "Allows a dealer to create a new bidding entry.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bidding created successfully",
                            content = @Content(schema = @Schema(implementation = Bidding.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden (only dealers allowed)", content = @Content)
            }
    )
    public ResponseEntity<Bidding> createBidding(
            @RequestBody BiddingDto biddingDto) {
        Bidding bidding = biddingService.createBidding(biddingDto);
        return ResponseEntity.ok(bidding);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('DEALER')")
    @Operation(
            summary = "Get all biddings with pagination",
            description = "Fetches a paginated list of all biddings created by dealers.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of biddings",
                            content = @Content(schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden (only dealers allowed)", content = @Content)
            }
    )
    public ResponseEntity<PageResponse<BiddingResponseDto>> getAllBiddings(
            @Parameter(description = "Page number (0-based index)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(biddingService.getAllBiddings(page, size));
    }
}
