package com.cartechindia.controller;

import com.cartechindia.entity.BidSchedule;
import com.cartechindia.service.BidScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bidschedule")
@RequiredArgsConstructor
public class BidScheduleController {

    private final BidScheduleService scheduleService;

    @GetMapping
    public List<BidSchedule> getAll() { return scheduleService.getAll(); }

    @PostMapping
    //@PreAuthorize("hasRole('DEALER')")
    public BidSchedule create(@RequestBody BidSchedule schedule) { return scheduleService.save(schedule); }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('DEALER')")
    public BidSchedule update(@PathVariable Long id, @RequestBody BidSchedule schedule) {
        schedule.setId(id);
        return scheduleService.save(schedule);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('DEALER')")
    public void delete(@PathVariable Long id) { scheduleService.delete(id); }
}
