package com.smartparking.dto;

import com.smartparking.domain.BookingStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class BookingDto {
    public Long id;
    public Long userId;
    public String userName;
    public Long vehicleId;
    public String plateNumber;
    public Long slotId;
    public String slotNumber;
    public LocalDateTime startAt;
    public LocalDateTime endAt;
    public BookingStatus status;
    public UUID bulkGroupId;
}
