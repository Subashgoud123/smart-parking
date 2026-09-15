package com.smartparking.dto;

import java.time.LocalDateTime;

public class TransactionDto {
    public Long id;
    public Long bookingId;
    public Long slotId;
    public String slotNumber;
    public Long vehicleId;
    public String plateNumber;
    public LocalDateTime entryTime;
    public LocalDateTime exitTime;
    public Long durationMinutes;
}
