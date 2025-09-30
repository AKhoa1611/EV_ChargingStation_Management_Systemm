package swp391.code.swp391.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.code.swp391.entity.ChargingStation;
import swp391.code.swp391.service.ChargingStationService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/charging-stations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChargingStationController {

    private final ChargingStationService chargingStationService;

    // 1. Lấy danh sách tất cả trạm sạc
    @GetMapping
    public ResponseEntity<List<ChargingStation>> getAllStations() {
        List<ChargingStation> stations = chargingStationService.getAllChargingStations();
        return ResponseEntity.ok(stations);
    }

    // 2. Lấy trạm sạc theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ChargingStation> getStationById(@PathVariable Long id) {
        Optional<ChargingStation> station = chargingStationService.getChargingStationById(id);
        return station.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // 3. Tạo trạm sạc mới
    @PostMapping
    public ResponseEntity<ChargingStation> createStation(
            @RequestParam String stationName,
            @RequestParam String address) {
        ChargingStation station = chargingStationService.createChargingStation(stationName, address);
        return ResponseEntity.ok(station);
    }

    // 4. Cập nhật thông tin trạm sạc
    @PutMapping("/{id}")
    public ResponseEntity<ChargingStation> updateStation(
            @PathVariable Long id,
            @RequestParam String stationName,
            @RequestParam String address,
            @RequestParam ChargingStation.ChargingStationStatus status) {
        try {
            ChargingStation station = chargingStationService.updateChargingStation(id, stationName, address, status);
            return ResponseEntity.ok(station);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Xóa trạm sạc
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStation(@PathVariable Long id) {
        try {
            chargingStationService.deleteChargingStation(id);
            return ResponseEntity.ok("Station deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. Tìm kiếm trạm theo tên (partial name)
    @GetMapping("/search")
    public ResponseEntity<List<ChargingStation>> searchByName(@RequestParam String name) {
        List<ChargingStation> stations = chargingStationService.searchByName(name);
        return ResponseEntity.ok(stations);
    }

    // 7. Lấy danh sách trạm theo trạng thái
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ChargingStation>> getStationsByStatus(
            @PathVariable ChargingStation.ChargingStationStatus status) {
        List<ChargingStation> stations = chargingStationService.getChargingStationsByStatus(status);
        return ResponseEntity.ok(stations);
    }


}
