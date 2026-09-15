package com.smartparking.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BookingRequest {
    @NotNull
    public Long vehicleId;
    @NotNull
    public Long slotId;
    @NotNull
    public LocalDateTime startAt;
    @NotNull
    public LocalDateTime endAt;
}
