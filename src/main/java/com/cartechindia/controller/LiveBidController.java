//package com.cartechindia.controller;
//
//import com.cartechindia.entity.LiveBid;
//import com.cartechindia.entity.BidSchedule;
//import com.cartechindia.entity.User;
//import com.cartechindia.repository.BidScheduleRepository;
//import com.cartechindia.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/bids")
//@RequiredArgsConstructor
//public class LiveBidController {
//
//    private final LiveBidService liveBidService;
//    private final BidScheduleRepository bidScheduleRepository;
//    private final UserRepository userRepository;
//
//    @PostMapping("/place")
//    public LiveBid placeBid(@RequestParam Long userId,
//                            @RequestParam Long scheduleId,
//                            @RequestParam Double amount) {
//
//        User buyer = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        BidSchedule schedule = bidScheduleRepository.findById(scheduleId)
//                .orElseThrow(() -> new RuntimeException("Bid schedule not found"));
//
//        return liveBidService.placeBid(buyer, schedule, amount);
//    }
//
//    // Finalize winner for a schedule
//    @PostMapping("/finalize/{scheduleId}")
//    public LiveBid finalizeWinner(@PathVariable Long scheduleId) {
//        BidSchedule schedule = bidScheduleRepository.findById(scheduleId)
//                .orElseThrow(() -> new RuntimeException("Bid schedule not found"));
//
//        return liveBidService.finalizeWinner(schedule);
//    }
//
//    // Get active bids for schedule
//    @GetMapping("/active/{scheduleId}")
//    public List<LiveBid> getActiveBids(@PathVariable Long scheduleId) {
//        BidSchedule schedule = bidScheduleRepository.findById(scheduleId)
//                .orElseThrow(() -> new RuntimeException("Bid schedule not found"));
//
//        return liveBidService.getActiveBids(schedule);
//    }
//}
