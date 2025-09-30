package swp391.code.swp391.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.code.swp391.dto.ChargingStationDTO;
import swp391.code.swp391.entity.ChargingStation;
import swp391.code.swp391.entity.ChargingStation.ChargingStationStatus;
import swp391.code.swp391.repository.ChargingStationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChargingStationServiceImpl implements ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;

    @Override
    public ChargingStationDTO createChargingStation(ChargingStationDTO chargingStationDTO) {
        // Kiểm tra station ID đã tồn tại
        if (chargingStationRepository.existsByStationId(chargingStationDTO.getStationId())) {
            throw new RuntimeException("Charging station with ID " + chargingStationDTO.getStationId() + " already exists");
        }

        // Kiểm tra tên station đã tồn tại
        if (chargingStationRepository.existsByStationName(chargingStationDTO.getStationName())) {
            throw new RuntimeException("Charging station with name '" + chargingStationDTO.getStationName() + "' already exists");
        }

        ChargingStation chargingStation = convertToEntity(chargingStationDTO);
        ChargingStation savedChargingStation = chargingStationRepository.save(chargingStation);
        return convertToDTO(savedChargingStation);
    }

    @Override
    @Transactional(readOnly = true)
    public ChargingStationDTO getChargingStationById(Long stationId) {
        ChargingStation chargingStation = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Charging station not found with id: " + stationId));
        return convertToDTO(chargingStation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingStationDTO> getAllChargingStations() {
        return chargingStationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChargingStationDTO updateChargingStation(Long stationId, ChargingStationDTO chargingStationDTO) {
        ChargingStation existingStation = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Charging station not found with id: " + stationId));

        // Kiểm tra tên mới có trùng với station khác không
        if (!existingStation.getStationName().equals(chargingStationDTO.getStationName()) &&
                chargingStationRepository.existsByStationName(chargingStationDTO.getStationName())) {
            throw new RuntimeException("Charging station with name '" + chargingStationDTO.getStationName() + "' already exists");
        }

        // Cập nhật thông tin
        existingStation.setStationName(chargingStationDTO.getStationName());
        existingStation.setAddress(chargingStationDTO.getAddress());
        existingStation.setStatus(chargingStationDTO.getStatus());

        ChargingStation updatedStation = chargingStationRepository.save(existingStation);
        return convertToDTO(updatedStation);
    }

    @Override
    public void deleteChargingStation(Long stationId) {
        ChargingStation chargingStation = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Charging station not found with id: " + stationId));

        // Kiểm tra có charging points không
        if (chargingStation.getChargingPoint() != null && !chargingStation.getChargingPoint().isEmpty()) {
            throw new RuntimeException("Cannot delete charging station. It has " +
                    chargingStation.getChargingPoint().size() + " charging point(s) associated");
        }

        chargingStationRepository.deleteById(stationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingStationDTO> searchChargingStationsByName(String stationName) {
        return chargingStationRepository.findByStationNameContainingIgnoreCase(stationName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingStationDTO> searchChargingStationsByAddress(String address) {
        return chargingStationRepository.findByAddressContainingIgnoreCase(address).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingStationDTO> getChargingStationsByStatus(ChargingStationStatus status) {
        return chargingStationRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChargingStationDTO updateChargingStationStatus(Long stationId, ChargingStationStatus status) {
        ChargingStation chargingStation = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Charging station not found with id: " + stationId));

        chargingStation.setStatus(status);
        ChargingStation updatedStation = chargingStationRepository.save(chargingStation);
        return convertToDTO(updatedStation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingStationDTO> getStationsWithAvailableChargingPoints() {
        return chargingStationRepository.findStationsWithAvailableChargingPoints().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingStationDTO> getStationsWithMinimumChargingPoints(int minPoints) {
        return chargingStationRepository.findStationsWithMinimumChargingPoints(minPoints).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingStationDTO> getStationsWithoutChargingPoints() {
        return chargingStationRepository.findStationsWithoutChargingPoints().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingStationDTO> getStationsByConnectorType(Long connectorTypeId) {
        return chargingStationRepository.findStationsByConnectorType(connectorTypeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingStationDTO> searchStationsByAddressAndStatus(String address, ChargingStationStatus status) {
        return chargingStationRepository.findByAddressContainingIgnoreCaseAndStatus(address, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingStationDTO> searchStationsByNameAndStatus(String stationName, ChargingStationStatus status) {
        return chargingStationRepository.findByStationNameContainingIgnoreCaseAndStatus(stationName, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countStationsByStatus(ChargingStationStatus status) {
        return chargingStationRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStationNameExists(String stationName) {
        return chargingStationRepository.existsByStationName(stationName);
    }

    // Helper methods
    private ChargingStation convertToEntity(ChargingStationDTO chargingStationDTO) {
        ChargingStation chargingStation = new ChargingStation();
        chargingStation.setStationId(chargingStationDTO.getStationId());
        chargingStation.setStationName(chargingStationDTO.getStationName());
        chargingStation.setAddress(chargingStationDTO.getAddress());
        chargingStation.setStatus(chargingStationDTO.getStatus() != null ?
                chargingStationDTO.getStatus() : ChargingStationStatus.ACTIVE);

        return chargingStation;
    }

    private ChargingStationDTO convertToDTO(ChargingStation chargingStation) {
        ChargingStationDTO dto = new ChargingStationDTO();
        dto.setStationId(chargingStation.getStationId());
        dto.setStationName(chargingStation.getStationName());
        dto.setAddress(chargingStation.getAddress());
        dto.setStatus(chargingStation.getStatus());

        // Cho response: set full charging points list
        dto.setChargingPoint(chargingStation.getChargingPoint());

        return dto;
    }
}