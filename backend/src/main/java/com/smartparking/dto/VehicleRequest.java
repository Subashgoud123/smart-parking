package com.smartparking.dto;

import com.smartparking.domain.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VehicleRequest {
    @NotBlank
    public String plateNumber;
    @NotNull
    public VehicleType vehicleType;
    public String contactPhone;
    public String nickname;
    public Long ownerId;
}
