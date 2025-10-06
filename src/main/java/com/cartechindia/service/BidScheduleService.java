package com.cartechindia.service;

import com.cartechindia.entity.BidSchedule;
import com.cartechindia.repository.BidScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidScheduleService {
    private final BidScheduleRepository bidScheduleRepository;

    public List<BidSchedule> getAll() { return bidScheduleRepository.findAll(); }
    public BidSchedule getById(Long id) { return bidScheduleRepository.findById(id).orElse(null); }

    public BidSchedule save(BidSchedule schedule) {
        // Only dealer can schedule bid
        if (schedule.getDealer() == null) throw new RuntimeException("Dealer must schedule the bid");
        // Auto status based on time
        LocalDateTime now = LocalDateTime.now();
        if (schedule.getStartTime().isBefore(now) && schedule.getEndTime().isAfter(now)) {
            schedule.setStatus(schedule.getStatus() == null ? schedule.getStatus() : schedule.getStatus());
        }
        return bidScheduleRepository.save(schedule);
    }

    public void delete(Long id) { bidScheduleRepository.deleteById(id); }
}
