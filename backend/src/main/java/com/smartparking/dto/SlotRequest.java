package com.smartparking.dto;

import com.smartparking.domain.SlotStatus;
import com.smartparking.domain.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SlotRequest {
    @NotBlank
    public String slotNumber;
    @NotBlank
    public String area;
    @NotBlank
    public String floor;
    @NotNull
    public VehicleType vehicleType;
    public SlotStatus status;
    public String notes;
}
