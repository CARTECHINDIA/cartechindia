package com.cartechindia.controller;

import com.cartechindia.dto.BiddingDto;
import com.cartechindia.dto.BiddingResponseDto;
import com.cartechindia.dto.PageResponse;
import com.cartechindia.entity.Bidding;
import com.cartechindia.service.BiddingService;
import com.cartechindia.service.impl.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/bidding")
@RequiredArgsConstructor
@Tag(name = "Bidding API", description = "Endpoints for managing biddings")
public class BiddingController {

    private final BiddingService biddingService;

    @PostMapping
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<Bidding> createBidding(
            @RequestBody BiddingDto biddingDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            throw new RuntimeException("Unauthorized: User not logged in");
        }

        Bidding bidding = biddingService.createBidding(biddingDto, userDetails.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(bidding);
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
