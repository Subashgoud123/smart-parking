package com.smartparking.service;

import com.smartparking.domain.*;
import com.smartparking.dto.BookingDto;
import com.smartparking.dto.DashboardStatsDto;
import com.smartparking.dto.NamedCountDto;
import com.smartparking.dto.SlotDetailDto;
import com.smartparking.mapper.DtoMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DashboardService {
    @Inject
    SlotService slotService;

    public DashboardStatsDto statistics() {
        DashboardStatsDto dto = new DashboardStatsDto();
        dto.totalSlots = ParkingSlot.count();
        dto.vacant = ParkingSlot.count("status", SlotStatus.VACANT);
        dto.occupied = ParkingSlot.count("status", SlotStatus.OCCUPIED);
        dto.reserved = ParkingSlot.count("status", SlotStatus.RESERVED);
        dto.preBooked = ParkingSlot.count("status", SlotStatus.PRE_BOOKED);
        dto.outOfService = ParkingSlot.count("status", SlotStatus.OUT_OF_SERVICE);
        dto.currentlyParked = ParkingTransaction.count("exitTime is null");
        dto.utilizationPercent = dto.totalSlots == 0 ? 0
                : Math.round((dto.occupied * 10000.0 / dto.totalSlots)) / 100.0;
        Map<String, Long> dist = new java.util.LinkedHashMap<>();
        for (VehicleType type : VehicleType.values()) {
            dist.put(type.name(), ParkingSlot.count("vehicleType", type));
        }
        dto.vehicleTypeDistribution = dist;
        dto.dailyBookings = lastDays(7);
        dto.monthlyBookings = lastMonths(12);
        return dto;
    }

    public SlotDetailDto slotDetail(Long id) {
        ParkingSlot slot = slotService.load(id);
        SlotDetailDto dto = new SlotDetailDto();
        dto.slot = DtoMapper.slot(slot);
        LocalDateTime now = LocalDateTime.now();
        ParkingBooking live = ParkingBooking.find(
                "slot.id = ?1 and status in (?2) and startAt <= ?3 and endAt >= ?3",
                id,
                List.of(BookingStatus.CONFIRMED, BookingStatus.PRE_BOOKED, BookingStatus.ACTIVE),
                now).firstResult();
        if (live != null) {
            dto.activeBooking = DtoMapper.booking(live);
        }
        ParkingTransaction open = ParkingTransaction.openForSlot(id);
        if (open != null) {
            dto.openTransaction = DtoMapper.tx(open);
        }
        return dto;
    }

    public List<BookingDto> historyForCurrent(Long userId) {
        return ParkingBooking.<ParkingBooking>list("user.id", userId).stream().map(DtoMapper::booking).toList();
    }

    private List<NamedCountDto> lastDays(int days) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;
        List<NamedCountDto> out = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            long count = ParkingBooking.count("createdAt >= ?1 and createdAt < ?2", d.atStartOfDay(), d.plusDays(1).atStartOfDay());
            out.add(new NamedCountDto(d.format(fmt), count));
        }
        return out;
    }

    private List<NamedCountDto> lastMonths(int months) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        List<NamedCountDto> out = new ArrayList<>();
        LocalDate cursor = LocalDate.now().withDayOfMonth(1);
        for (int i = months - 1; i >= 0; i--) {
            LocalDate start = cursor.minusMonths(i);
            LocalDate end = start.plusMonths(1);
            long count = ParkingBooking.count("createdAt >= ?1 and createdAt < ?2", start.atStartOfDay(), end.atStartOfDay());
            out.add(new NamedCountDto(start.format(fmt), count));
        }
        return out;
    }
}
