package com.cartechindia.controller;

import com.cartechindia.dto.*;
import com.cartechindia.service.BiddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("bidding")
@RequiredArgsConstructor
public class BiddingController {

    private final BiddingService biddingService;

    // schedule a bidding for an existing car
    @PostMapping("/schedule/{carId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<BiddingDto>> scheduleBidding(
            @PathVariable Long carId,
            @RequestBody BiddingDto dto,
            Authentication authentication
    ) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        BiddingDto out = biddingService.scheduleBidding(carId, dto, email);
        return ResponseEntity.ok(new ApiResponse<>(200, "Bidding scheduled", out));
    }

    // place a bid (active window only)
    @PostMapping("/place/{biddingId}")
    @PreAuthorize("hasAnyRole('DEALER','ADMIN')")
    public ResponseEntity<ApiResponse<BidResponseDto>> placeBid(
            @PathVariable Long biddingId,
            @RequestParam BigDecimal amount,
            Authentication authentication
    ) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        BidRequestDto req = new BidRequestDto();
        req.setBiddingId(biddingId);
        req.setBidAmount(amount);
        BidResponseDto out = biddingService.placeBid(req, email);
        return ResponseEntity.ok(new ApiResponse<>(200, "Bid placed", out));
    }

    // get live bidding details (participant / non-participant view)
    @GetMapping("/{biddingId}")
    @PreAuthorize("hasAnyRole('ADMIN','DEALER','SELLER')")
    public ResponseEntity<ApiResponse<BiddingResponseDto>> getBiddingDetails(
            @PathVariable Long biddingId,
            Authentication authentication
    ) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        BiddingResponseDto out = biddingService.getBiddingDetails(biddingId, email);
        return ResponseEntity.ok(new ApiResponse<>(200, "Bidding details", out));
    }

    // list all biddings (brief)
    @GetMapping("all")
    @PreAuthorize("hasAnyRole('ADMIN','DEALER','SELLER')")
    public ResponseEntity<ApiResponse<List<BiddingDto>>> getAll() {
        List<BiddingDto> all = biddingService.getAllBiddings();
        return ResponseEntity.ok(new ApiResponse<>(200, "All biddings", all));
    }

}
