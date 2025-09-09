package com.cartechindia.controller;

import com.cartechindia.dto.LiveBiddingRequestDto;
import com.cartechindia.dto.LiveBiddingResponseDto;
import com.cartechindia.service.LiveBiddingService;
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
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/live-bidding")
@Tag(name = "Live Bidding API", description = "Endpoints to manage live bids")
@RequiredArgsConstructor
public class LiveBiddingController {

    private final LiveBiddingService liveBiddingService;

    @Operation(
            summary = "Place a new bid",
            description = "Allows a dealer to place a bid in a given bidding session.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bid placed successfully",
                            content = @Content(schema = @Schema(implementation = LiveBiddingResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden (only dealers allowed)", content = @Content)
            }
    )
    @PreAuthorize("hasRole('DEALER')")
    @PostMapping
    public ResponseEntity<LiveBiddingResponseDto> placeBid(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bid details including biddingId, userId and amount",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LiveBiddingRequestDto.class))
            )
            @RequestBody LiveBiddingRequestDto dto) {
        return ResponseEntity.ok(liveBiddingService.placeBid(dto));
    }

    @Operation(
            summary = "Get all bids for a bidding",
            description = "Returns all bids for the specified bidding, sorted by bid amount descending.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of bids retrieved successfully",
                            content = @Content(schema = @Schema(implementation = LiveBiddingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Bidding not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden (only dealers allowed)", content = @Content)
            }
    )
    @PreAuthorize("hasRole('DEALER')")
    @GetMapping("/{biddingId}")
    public ResponseEntity<List<LiveBiddingResponseDto>> getAllBids(
            @Parameter(description = "The ID of the bidding session", required = true, example = "101")
            @PathVariable Long biddingId) {
        return ResponseEntity.ok(liveBiddingService.getAllBids(biddingId));
    }

    @Operation(
            summary = "Get highest bid for a bidding",
            description = "Fetches the highest bid placed in a given bidding session.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Highest bid retrieved successfully",
                            content = @Content(schema = @Schema(implementation = LiveBiddingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "No bids found for this bidding", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden (only dealers allowed)", content = @Content)
            }
    )
    @PreAuthorize("hasRole('DEALER')")
    @GetMapping("/{biddingId}/highest")
    public ResponseEntity<LiveBiddingResponseDto> getHighestBid(
            @Parameter(description = "The ID of the bidding session", required = true, example = "101")
            @PathVariable Long biddingId) {
        return ResponseEntity.ok(liveBiddingService.getHighestBid(biddingId));
    }
}
