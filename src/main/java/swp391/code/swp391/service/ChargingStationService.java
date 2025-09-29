package swp391.code.swp391.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp391.code.swp391.entity.ChargingStation;
import swp391.code.swp391.repository.ChargingStationRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;

    // Lấy tất cả trạm sạc
    public List<ChargingStation> getAllChargingStations() {
        return chargingStationRepository.findAll();
    }

    // Lấy trạm sạc theo ID
    public Optional<ChargingStation> getChargingStationById(Long id) {
        return chargingStationRepository.findById(id);
    }

    // Tạo trạm sạc mới
    public ChargingStation createChargingStation(String stationName, String address) {
        ChargingStation station = new ChargingStation();
        station.setStationName(stationName);
        station.setAddress(address);
        station.setStatus(ChargingStation.ChargingStationStatus.ACTIVE);
        return chargingStationRepository.save(station);
    }

    // Cập nhật thông tin trạm sạc
    public ChargingStation updateChargingStation(Long id, String stationName, String address, ChargingStation.ChargingStationStatus status) {
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + id));

        station.setStationName(stationName);
        station.setAddress(address);
        station.setStatus(status);
        return chargingStationRepository.save(station);
    }

    // Xóa trạm sạc
    public void deleteChargingStation(Long id) {
        chargingStationRepository.deleteById(id);
    }

    // Tìm kiếm theo tên
    public List<ChargingStation> searchByName(String name) {
        return chargingStationRepository.findByStationNameContainingIgnoreCase(name);
    }

    // Lấy danh sách theo trạng thái
    public List<ChargingStation> getChargingStationsByStatus(ChargingStation.ChargingStationStatus status) {
        return chargingStationRepository.findByStatus(status);
    }
}
