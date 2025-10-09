package com.cartechindia.controller;

import com.cartechindia.dto.request.BidScheduleRequestDto;
import com.cartechindia.dto.response.BidScheduleResponseDto;
import com.cartechindia.entity.BidSchedule;
import com.cartechindia.service.BidScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bidschedule")
@RequiredArgsConstructor
public class BidScheduleController {

    private final BidScheduleService bidScheduleService;

    /*
//    @GetMapping
//    public List<BidSchedule> getAll() { return bidScheduleService.getAll(); }

//    @PostMapping
//    //@PreAuthorize("hasRole('DEALER')")
//    public BidSchedule create(@RequestBody BidSchedule schedule) { return bidScheduleService.save(schedule); }


    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('DEALER')")
    public BidSchedule update(@PathVariable Long id, @RequestBody BidSchedule schedule) {
        schedule.setId(id);
        return bidScheduleService.save(schedule);
    }

//    @DeleteMapping("/{id}")
//    //@PreAuthorize("hasRole('DEALER')")
//    public void delete(@PathVariable Long id) { bidScheduleService.delete(id); }
    */

    //===========================

    @PostMapping("/schedule")
    public ResponseEntity<BidScheduleResponseDto> createBidSchedule(
            @RequestBody @Valid BidScheduleRequestDto requestDto) {

        BidScheduleResponseDto responseDto = bidScheduleService.createBidSchedule(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
