package com.cartechindia.service;

import com.cartechindia.entity.CarMasterData;
import com.cartechindia.repository.CarMasterDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarMasterDataService {
    private final CarMasterDataRepository masterDataRepository;

    public List<CarMasterData> getAll() { return masterDataRepository.findAll(); }
    public CarMasterData getById(Long id) { return masterDataRepository.findById(id).orElse(null); }
    public CarMasterData save(CarMasterData data) { return masterDataRepository.save(data); }
    public void delete(Long id) { masterDataRepository.deleteById(id); }
}
