package com.cartechindia.controller;

import com.cartechindia.dto.request.BidScheduleDto;
import com.cartechindia.dto.request.LiveBidRequestDto;
import com.cartechindia.dto.response.ApiResponse;
import com.cartechindia.dto.response.BidScheduleResponseDto;
import com.cartechindia.dto.response.LiveBidResponseDto;
import com.cartechindia.service.BidScheduleService;
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
public class BidScheduleController {

    private final BidScheduleService bidScheduleService;

    // schedule a bidding for an existing car
    @PostMapping("/schedule/{carId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<BidScheduleDto>> scheduleBidding(
            @PathVariable Long carId,
            @RequestBody BidScheduleDto dto,
            Authentication authentication
    ) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        BidScheduleDto out = bidScheduleService.scheduleBidding(carId, dto, email);
        return ResponseEntity.ok(new ApiResponse<>(200, "Bidding scheduled", out));
    }

    // place a bid (active window only)
    @PostMapping("/place/{biddingId}")
    @PreAuthorize("hasAnyRole('DEALER','ADMIN')")
    public ResponseEntity<ApiResponse<LiveBidResponseDto>> placeBid(
            @PathVariable Long biddingId,
            @RequestParam BigDecimal amount,
            Authentication authentication
    ) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        LiveBidRequestDto req = new LiveBidRequestDto();
        req.setBiddingId(biddingId);
        req.setBidAmount(amount);
        LiveBidResponseDto out = bidScheduleService.placeBid(req, email);
        return ResponseEntity.ok(new ApiResponse<>(200, "Bid placed", out));
    }

    // get live bidding details (participant / non-participant view)
    @GetMapping("/{biddingId}")
    @PreAuthorize("hasAnyRole('ADMIN','DEALER','SELLER')")
    public ResponseEntity<ApiResponse<BidScheduleResponseDto>> getBiddingDetails(
            @PathVariable Long biddingId,
            Authentication authentication
    ) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        BidScheduleResponseDto out = bidScheduleService.getBiddingDetails(biddingId, email);
        return ResponseEntity.ok(new ApiResponse<>(200, "Bidding details", out));
    }

    // list all biddings (brief)
    @GetMapping("all")
    @PreAuthorize("hasAnyRole('ADMIN','DEALER','SELLER')")
    public ResponseEntity<ApiResponse<List<BidScheduleDto>>> getAll() {
        List<BidScheduleDto> all = bidScheduleService.getAllBiddings();
        return ResponseEntity.ok(new ApiResponse<>(200, "All biddings", all));
    }

}
