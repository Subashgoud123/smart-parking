package com.smartparking.service;

import com.smartparking.domain.UserAccount;
import com.smartparking.domain.Vehicle;
import com.smartparking.dto.VehicleDto;
import com.smartparking.dto.VehicleRequest;
import com.smartparking.exception.ApiException;
import com.smartparking.mapper.DtoMapper;
import com.smartparking.security.CurrentUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class VehicleService {
    @Inject
    CurrentUser current;

    public List<VehicleDto> list() {
        if (current.isStaff()) {
            return Vehicle.<Vehicle>listAll().stream().map(DtoMapper::vehicle).toList();
        }
        return Vehicle.<Vehicle>list("owner.id", current.id()).stream().map(DtoMapper::vehicle).toList();
    }

    public VehicleDto get(Long id) {
        Vehicle v = load(id);
        current.requireOwnerOrStaff(v.owner.id);
        return DtoMapper.vehicle(v);
    }

    @Transactional
    public VehicleDto create(VehicleRequest req) {
        String plate = normalize(req.plateNumber);
        if (Vehicle.findByPlate(plate) != null) {
            throw ApiException.conflict("Plate already registered");
        }
        UserAccount owner = current.account();
        if (req.ownerId != null && current.isStaff()) {
            owner = UserAccount.findById(req.ownerId);
            if (owner == null) {
                throw ApiException.notFound("Owner not found");
            }
        }
        Vehicle v = new Vehicle();
        v.plateNumber = plate;
        v.vehicleType = req.vehicleType;
        v.owner = owner;
        v.contactPhone = req.contactPhone == null ? owner.phone : req.contactPhone;
        v.nickname = req.nickname;
        v.persist();
        return DtoMapper.vehicle(v);
    }

    @Transactional
    public VehicleDto update(Long id, VehicleRequest req) {
        Vehicle v = load(id);
        current.requireOwnerOrStaff(v.owner.id);
        String plate = normalize(req.plateNumber);
        Vehicle other = Vehicle.findByPlate(plate);
        if (other != null && !other.id.equals(id)) {
            throw ApiException.conflict("Plate already registered");
        }
        v.plateNumber = plate;
        v.vehicleType = req.vehicleType;
        v.contactPhone = req.contactPhone;
        v.nickname = req.nickname;
        return DtoMapper.vehicle(v);
    }

    @Transactional
    public void delete(Long id) {
        Vehicle v = load(id);
        current.requireOwnerOrStaff(v.owner.id);
        v.delete();
    }

    private Vehicle load(Long id) {
        Vehicle v = Vehicle.findById(id);
        if (v == null) {
            throw ApiException.notFound("Vehicle not found");
        }
        return v;
    }

    private String normalize(String plate) {
        return plate.trim().toUpperCase().replace(" ", "");
    }
}
