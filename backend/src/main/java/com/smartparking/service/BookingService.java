package com.smartparking.service;

import com.smartparking.domain.*;
import com.smartparking.dto.BookingDto;
import com.smartparking.dto.BookingRequest;
import com.smartparking.dto.BulkBookingRequest;
import com.smartparking.exception.ApiException;
import com.smartparking.mapper.DtoMapper;
import com.smartparking.security.CurrentUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BookingService {
    @Inject
    CurrentUser current;
    @Inject
    SlotService slots;

    public List<BookingDto> list() {
        if (current.isStaff()) {
            return ParkingBooking.<ParkingBooking>listAll().stream().map(DtoMapper::booking).toList();
        }
        return ParkingBooking.<ParkingBooking>list("user.id", current.id()).stream().map(DtoMapper::booking).toList();
    }

    public BookingDto get(Long id) {
        ParkingBooking b = load(id);
        current.requireOwnerOrStaff(b.user.id);
        return DtoMapper.booking(b);
    }

    @Transactional
    public BookingDto create(BookingRequest req) {
        return DtoMapper.booking(createInternal(req.vehicleId, req.slotId, req.startAt, req.endAt, null));
    }

    @Transactional
    public List<BookingDto> bulk(BulkBookingRequest req) {
        if (req.vehicleIds.size() != req.slotIds.size()) {
            throw ApiException.badRequest("Each vehicle must be paired with one slot (same list length)");
        }
        UUID group = UUID.randomUUID();
        List<ParkingBooking> created = new ArrayList<>();
        for (int i = 0; i < req.vehicleIds.size(); i++) {
            created.add(createInternal(req.vehicleIds.get(i), req.slotIds.get(i), req.startAt, req.endAt, group));
        }
        return created.stream().map(DtoMapper::booking).toList();
    }

    @Transactional
    public BookingDto cancel(Long id) {
        ParkingBooking b = load(id);
        current.requireOwnerOrStaff(b.user.id);
        if (b.status == BookingStatus.CANCELLED || b.status == BookingStatus.COMPLETED) {
            throw ApiException.conflict("Booking cannot be cancelled");
        }
        if (b.status == BookingStatus.ACTIVE) {
            throw ApiException.conflict("Vehicle is currently parked. Process exit first.");
        }
        b.status = BookingStatus.CANCELLED;
        if (noOtherLiveBooking(b.slot.id, b.id)) {
            b.slot.status = SlotStatus.VACANT;
        }
        return DtoMapper.booking(b);
    }

    ParkingBooking load(Long id) {
        ParkingBooking b = ParkingBooking.findById(id);
        if (b == null) {
            throw ApiException.notFound("Booking not found");
        }
        return b;
    }

    private ParkingBooking createInternal(Long vehicleId, Long slotId, LocalDateTime start, LocalDateTime end, UUID group) {
        if (!end.isAfter(start)) {
            throw ApiException.badRequest("End time must be after start time");
        }
        Vehicle vehicle = Vehicle.findById(vehicleId);
        if (vehicle == null) {
            throw ApiException.notFound("Vehicle not found");
        }
        current.requireOwnerOrStaff(vehicle.owner.id);
        ParkingSlot slot = slots.load(slotId);
        if (slot.status == SlotStatus.OUT_OF_SERVICE) {
            throw ApiException.conflict("Slot is out of service");
        }
        if (slot.vehicleType != vehicle.vehicleType) {
            throw ApiException.badRequest("Vehicle type does not match this slot");
        }
        if (hasOverlap(slot.id, start, end, null)) {
            throw ApiException.conflict("Slot " + slot.slotNumber + " is not available for the selected window");
        }
        boolean future = start.isAfter(LocalDateTime.now().plusMinutes(15));
        ParkingBooking b = new ParkingBooking();
        b.user = current.account();
        if (current.isStaff()) {
            b.user = vehicle.owner;
        }
        b.vehicle = vehicle;
        b.slot = slot;
        b.startAt = start;
        b.endAt = end;
        b.bulkGroupId = group;
        b.status = future ? BookingStatus.PRE_BOOKED : BookingStatus.CONFIRMED;
        slot.status = future ? SlotStatus.PRE_BOOKED : SlotStatus.RESERVED;
        b.persist();
        return b;
    }

    boolean hasOverlap(Long slotId, LocalDateTime start, LocalDateTime end, Long excludeBookingId) {
        List<ParkingBooking> live = ParkingBooking.list(
                "slot.id = ?1 and status in (?2)",
                slotId,
                List.of(BookingStatus.CONFIRMED, BookingStatus.PRE_BOOKED, BookingStatus.ACTIVE));
        return live.stream()
                .filter(b -> excludeBookingId == null || !b.id.equals(excludeBookingId))
                .anyMatch(b -> b.startAt.isBefore(end) && b.endAt.isAfter(start));
    }

    private boolean noOtherLiveBooking(Long slotId, Long excludeId) {
        return ParkingBooking.count(
                "slot.id = ?1 and id <> ?2 and status in (?3)",
                slotId,
                excludeId,
                List.of(BookingStatus.CONFIRMED, BookingStatus.PRE_BOOKED, BookingStatus.ACTIVE)) == 0;
    }
}
