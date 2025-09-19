import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.util.CarSellingProjection;

private CarSellingDto mapProjectionToDto(CarSellingProjection projection) {

    CarSellingDto dto = new CarSellingDto();

    dto.setId(projection.getId());
    dto.setRegNumber(projection.getRegNumber());
    dto.setCarId(projection.getCarId());
    dto.setManufactureYear(projection.getManufactureYear());
    dto.setKmDriven(projection.getKmDriven());
    dto.setColor(projection.getColor());
    dto.setOwners(projection.getOwners());
    dto.setPrice(projection.getPrice());
    dto.setHealth(projection.getHealth());
    dto.setInsurance(projection.getInsurance());
    dto.setRegistrationDate(projection.getRegistrationDate());
    dto.setState(projection.getState());
    dto.setCity(projection.getCity());
    dto.setStatus(projection.getStatus());

    // Joined fields from CARS
    dto.setBrand(projection.getBrand());
    dto.setModel(projection.getModel());
    dto.setVariant(projection.getVariant());
    dto.setFuelType(projection.getFuelType());
    dto.setTransmission(projection.getTransmission());
    dto.setBodyType(projection.getBodyType());
    dto.setCreatedAt(projection.getCreatedAt());

    // imageUrls will be set separately (after fetching Images)
    return dto;
}

void main() {
}
