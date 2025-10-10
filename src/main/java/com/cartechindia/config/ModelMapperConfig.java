package com.cartechindia.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Skip mapping images in CarSelling -> CarSellingDto
//        modelMapper.typeMap(CarSelling.class, CarSellingDto.class)
//                .addMappings(m ->
//                        m.skip(CarSellingDto::setImages));

        // Strict matching: DTO and Entity field names must match exactly
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);  // ignore nulls when mapping
        return modelMapper;
    }
}
