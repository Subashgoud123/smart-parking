package com.smartparking.service;

import com.smartparking.domain.ParkingSlot;
import com.smartparking.domain.SlotStatus;
import com.smartparking.domain.VehicleType;
import com.smartparking.dto.SlotDto;
import com.smartparking.dto.SlotRequest;
import com.smartparking.exception.ApiException;
import com.smartparking.mapper.DtoMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class SlotService {
    public List<SlotDto> list() {
        return ParkingSlot.<ParkingSlot>listAll().stream().map(DtoMapper::slot).toList();
    }

    public List<SlotDto> available(VehicleType type) {
        if (type == null) {
            return ParkingSlot.<ParkingSlot>list("status", SlotStatus.VACANT).stream().map(DtoMapper::slot).toList();
        }
        return ParkingSlot.<ParkingSlot>list("status = ?1 and vehicleType = ?2", SlotStatus.VACANT, type)
                .stream().map(DtoMapper::slot).toList();
    }

    public SlotDto get(Long id) {
        return DtoMapper.slot(load(id));
    }

    @Transactional
    public SlotDto create(SlotRequest req) {
        if (ParkingSlot.find("slotNumber", req.slotNumber.trim().toUpperCase()).firstResult() != null) {
            throw ApiException.conflict("Slot number already exists");
        }
        ParkingSlot slot = new ParkingSlot();
        apply(slot, req);
        slot.persist();
        return DtoMapper.slot(slot);
    }

    @Transactional
    public SlotDto update(Long id, SlotRequest req) {
        ParkingSlot slot = load(id);
        ParkingSlot other = ParkingSlot.find("slotNumber", req.slotNumber.trim().toUpperCase()).firstResult();
        if (other != null && !other.id.equals(id)) {
            throw ApiException.conflict("Slot number already exists");
        }
        apply(slot, req);
        return DtoMapper.slot(slot);
    }

    @Transactional
    public void delete(Long id) {
        load(id).delete();
    }

    ParkingSlot load(Long id) {
        ParkingSlot slot = ParkingSlot.findById(id);
        if (slot == null) {
            throw ApiException.notFound("Parking slot not found");
        }
        return slot;
    }

    private void apply(ParkingSlot slot, SlotRequest req) {
        slot.slotNumber = req.slotNumber.trim().toUpperCase();
        slot.area = req.area.trim();
        slot.floor = req.floor.trim();
        slot.vehicleType = req.vehicleType;
        slot.status = req.status == null ? SlotStatus.VACANT : req.status;
        slot.notes = req.notes;
    }
}
