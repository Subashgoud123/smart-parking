package com.smartparking.dto;

import com.smartparking.domain.SlotStatus;
import com.smartparking.domain.VehicleType;

public class SlotDto {
    public Long id;
    public String slotNumber;
    public String area;
    public String floor;
    public VehicleType vehicleType;
    public SlotStatus status;
    public String notes;
}
