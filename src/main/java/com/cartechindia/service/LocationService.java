package com.cartechindia.service;

import com.cartechindia.entity.Location;
import com.cartechindia.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    public List<Location> getAll() { return locationRepository.findAll(); }
    public Location getById(Long id) { return locationRepository.findById(id).orElse(null); }
    public Location save(Location loc) { return locationRepository.save(loc); }
    public void delete(Long id) { locationRepository.deleteById(id); }
}
