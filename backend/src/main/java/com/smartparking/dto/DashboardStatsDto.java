package com.smartparking.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDto {
    public long totalSlots;
    public long vacant;
    public long occupied;
    public long reserved;
    public long preBooked;
    public long outOfService;
    public long currentlyParked;
    public double utilizationPercent;
    public Map<String, Long> vehicleTypeDistribution;
    public List<NamedCountDto> dailyBookings;
    public List<NamedCountDto> monthlyBookings;
}
