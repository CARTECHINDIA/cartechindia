//package com.cartechindia.controller;
//
//import com.cartechindia.entity.Bid;
//import com.cartechindia.entity.User;
//import com.cartechindia.service.BidService;
//import com.cartechindia.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//import java.math.BigDecimal;
//import java.util.List;
//
//@RestController
//@RequestMapping("bid")
//@RequiredArgsConstructor
//public class BidController {
//
//    private final BidService bidService;
//    private final UserService userService;
//
//    @PostMapping("/{biddingId}")
//    @PreAuthorize("hasAnyRole('DEALER', 'ADMIN', 'SELLER')")
//    public ResponseEntity<Bid> placeBid(
//            @PathVariable Long biddingId,
//            @RequestParam BigDecimal amount,
//            @AuthenticationPrincipal UserDetails userDetails // Spring Security principal
//    ) {
//        User user = userService.findByEmail(userDetails.getUsername());
//        Bid bid = bidService.placeBid(biddingId, user, amount);
//        return ResponseEntity.ok(bid);
//    }
//
//
//    // 🔹 Get all bids for a bidding session
//    @GetMapping("/{biddingId}")
//    @PreAuthorize("hasAnyRole('DEALER', 'ADMIN', 'SELLER')")
//    public ResponseEntity<List<Bid>> getBids(@PathVariable Long biddingId) {
//        List<Bid> bids = bidService.getBidsForBidding(biddingId);
//        return ResponseEntity.ok(bids);
//    }
//
//    // 🔹 Get highest bid
//    @GetMapping("/{biddingId}/highest")
//    @PreAuthorize("hasAnyRole('DEALER', 'ADMIN', 'SELLER')")
//    public ResponseEntity<Bid> getHighestBid(@PathVariable Long biddingId) {
//        Bid highest = bidService.getHighestBid(biddingId);
//        return ResponseEntity.ok(highest);
//    }
//}
