package swp391.code.swp391.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.code.swp391.dto.ChargingPointDTO;
import swp391.code.swp391.entity.ChargingPoint;
import swp391.code.swp391.entity.ChargingPoint.ChargingPointStatus;
import swp391.code.swp391.entity.ChargingStation;
import swp391.code.swp391.repository.ChargingPointRepository;
import swp391.code.swp391.repository.ChargingStationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChargingPointServiceImpl implements ChargingPointService {

    private final ChargingPointRepository chargingPointRepository;
    private final ChargingStationRepository chargingStationRepository;

    @Override
    public ChargingPointDTO createChargingPoint(ChargingPointDTO chargingPointDTO) {
        // Kiểm tra charging point ID đã tồn tại
        if (chargingPointRepository.existsByChargingPointId(chargingPointDTO.getChargingPointId())) {
            throw new RuntimeException("Charging point with ID " + chargingPointDTO.getChargingPointId() + " already exists");
        }

        ChargingPoint chargingPoint = convertToEntity(chargingPointDTO);
        ChargingPoint savedChargingPoint = chargingPointRepository.save(chargingPoint);
        return convertToDTO(savedChargingPoint);
    }

    @Override
    @Transactional(readOnly = true)
    public ChargingPointDTO getChargingPointById(Long chargingPointId) {
        ChargingPoint chargingPoint = chargingPointRepository.findById(chargingPointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found with id: " + chargingPointId));
        return convertToDTO(chargingPoint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingPointDTO> getAllChargingPoints() {
        return chargingPointRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingPointDTO> getChargingPointsByStationId(Long stationId) {
        return chargingPointRepository.findByStationStationId(stationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChargingPointDTO updateChargingPoint(Long chargingPointId, ChargingPointDTO chargingPointDTO) {
        ChargingPoint existingChargingPoint = chargingPointRepository.findById(chargingPointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found with id: " + chargingPointId));

        // Cập nhật status
        existingChargingPoint.setStatus(chargingPointDTO.getStatus());

        // Cập nhật station nếu có
        if (chargingPointDTO.getStationId() != null) {
            ChargingStation station = chargingStationRepository.findById(chargingPointDTO.getStationId())
                    .orElseThrow(() -> new RuntimeException("Charging station not found with id: " + chargingPointDTO.getStationId()));
            existingChargingPoint.setStation(station);
        }

        ChargingPoint updatedChargingPoint = chargingPointRepository.save(existingChargingPoint);
        return convertToDTO(updatedChargingPoint);
    }

    @Override
    public void deleteChargingPoint(Long chargingPointId) {
        ChargingPoint chargingPoint = chargingPointRepository.findById(chargingPointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found with id: " + chargingPointId));

        // Kiểm tra có connector types không
        if (chargingPoint.getConnectorTypes() != null && !chargingPoint.getConnectorTypes().isEmpty()) {
            throw new RuntimeException("Cannot delete charging point. It has " +
                    chargingPoint.getConnectorTypes().size() + " connector type(s) associated");
        }

        chargingPointRepository.deleteById(chargingPointId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingPointDTO> getChargingPointsByStatus(ChargingPointStatus status) {
        return chargingPointRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingPointDTO> getChargingPointsByStationAndStatus(Long stationId, ChargingPointStatus status) {
        return chargingPointRepository.findByStationStationIdAndStatus(stationId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChargingPointDTO updateChargingPointStatus(Long chargingPointId, ChargingPointStatus status) {
        ChargingPoint chargingPoint = chargingPointRepository.findById(chargingPointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found with id: " + chargingPointId));

        chargingPoint.setStatus(status);
        ChargingPoint updatedChargingPoint = chargingPointRepository.save(chargingPoint);
        return convertToDTO(updatedChargingPoint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingPointDTO> getAvailableChargingPointsWithConnectors() {
        return chargingPointRepository.findAvailableChargingPointsWithConnectors().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingPointDTO> getChargingPointsByConnectorType(Long connectorTypeId) {
        return chargingPointRepository.findByConnectorTypeId(connectorTypeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargingPointDTO> getChargingPointsWithoutConnectors() {
        return chargingPointRepository.findChargingPointsWithoutConnectors().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countChargingPointsByStatus(ChargingPointStatus status) {
        return chargingPointRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countChargingPointsByStation(Long stationId) {
        return chargingPointRepository.countByStationStationId(stationId);
    }

    // Helper methods
    private ChargingPoint convertToEntity(ChargingPointDTO chargingPointDTO) {
        ChargingPoint chargingPoint = new ChargingPoint();
        chargingPoint.setChargingPointId(chargingPointDTO.getChargingPointId());
        chargingPoint.setStatus(chargingPointDTO.getStatus() != null ?
                chargingPointDTO.getStatus() : ChargingPointStatus.AVAILABLE);

        // Set station
        if (chargingPointDTO.getStationId() != null) {
            ChargingStation station = chargingStationRepository.findById(chargingPointDTO.getStationId())
                    .orElseThrow(() -> new RuntimeException("Charging station not found with id: " + chargingPointDTO.getStationId()));
            chargingPoint.setStation(station);
        }

        return chargingPoint;
    }

    private ChargingPointDTO convertToDTO(ChargingPoint chargingPoint) {
        ChargingPointDTO dto = new ChargingPointDTO();
        dto.setChargingPointId(chargingPoint.getChargingPointId());
        dto.setStatus(chargingPoint.getStatus());

        // Cho response: set full objects
        dto.setStation(chargingPoint.getStation());
        dto.setConnectorTypes(chargingPoint.getConnectorTypes());

        return dto;
    }
}