package com.smartparking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class BulkBookingRequest {
    @NotEmpty
    public List<Long> vehicleIds;
    @NotEmpty
    public List<Long> slotIds;
    @NotNull
    public LocalDateTime startAt;
    @NotNull
    public LocalDateTime endAt;
}
