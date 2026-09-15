package com.smartparking.service;

import com.smartparking.domain.*;
import com.smartparking.dto.EntryRequest;
import com.smartparking.dto.ExitRequest;
import com.smartparking.dto.TransactionDto;
import com.smartparking.exception.ApiException;
import com.smartparking.mapper.DtoMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class GateService {
    @Inject
    BookingService bookings;

    @Transactional
    public TransactionDto entry(EntryRequest req) {
        ParkingBooking booking = resolveBooking(req);
        if (booking.status == BookingStatus.CANCELLED || booking.status == BookingStatus.COMPLETED) {
            throw ApiException.conflict("Booking is not valid for entry");
        }
        if (ParkingTransaction.openForVehicle(booking.vehicle.id) != null) {
            throw ApiException.conflict("Vehicle already has an open parking session");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(booking.endAt)) {
            throw ApiException.conflict("Booking window has expired");
        }
        booking.status = BookingStatus.ACTIVE;
        booking.slot.status = SlotStatus.OCCUPIED;
        ParkingTransaction tx = new ParkingTransaction();
        tx.booking = booking;
        tx.slot = booking.slot;
        tx.vehicle = booking.vehicle;
        tx.entryTime = now;
        tx.persist();
        return DtoMapper.tx(tx);
    }

    @Transactional
    public TransactionDto exit(ExitRequest req) {
        ParkingTransaction tx = resolveOpenTx(req);
        LocalDateTime now = LocalDateTime.now();
        tx.exitTime = now;
        tx.durationMinutes = Math.max(1, Duration.between(tx.entryTime, now).toMinutes());
        tx.slot.status = SlotStatus.VACANT;
        if (tx.booking != null) {
            tx.booking.status = BookingStatus.COMPLETED;
        }
        return DtoMapper.tx(tx);
    }

    public List<TransactionDto> openSessions() {
        return ParkingTransaction.<ParkingTransaction>list("exitTime is null").stream().map(DtoMapper::tx).toList();
    }

    private ParkingBooking resolveBooking(EntryRequest req) {
        if (req.bookingId != null) {
            return bookings.load(req.bookingId);
        }
        Vehicle vehicle = null;
        if (req.plateNumber != null && !req.plateNumber.isBlank()) {
            vehicle = Vehicle.findByPlate(req.plateNumber.trim().toUpperCase().replace(" ", ""));
            if (vehicle == null) {
                throw ApiException.notFound("Vehicle not found for plate");
            }
        }
        ParkingSlot slot = null;
        if (req.slotNumber != null && !req.slotNumber.isBlank()) {
            slot = ParkingSlot.find("slotNumber", req.slotNumber.trim().toUpperCase()).firstResult();
            if (slot == null) {
                throw ApiException.notFound("Slot not found");
            }
        }
        if (vehicle == null && slot == null) {
            throw ApiException.badRequest("Provide bookingId, plateNumber, or slotNumber");
        }
        LocalDateTime now = LocalDateTime.now();
        String query = "status in (?1) and startAt <= ?2 and endAt >= ?2";
        List<Object> params = new java.util.ArrayList<>();
        params.add(List.of(BookingStatus.CONFIRMED, BookingStatus.PRE_BOOKED, BookingStatus.ACTIVE));
        params.add(now);
        if (vehicle != null) {
            query += " and vehicle.id = ?" + (params.size() + 1);
            params.add(vehicle.id);
        }
        if (slot != null) {
            query += " and slot.id = ?" + (params.size() + 1);
            params.add(slot.id);
        }
        ParkingBooking booking = ParkingBooking.find(query, params.toArray()).firstResult();
        if (booking == null) {
            throw ApiException.notFound("No valid booking for this vehicle/slot");
        }
        return booking;
    }

    private ParkingTransaction resolveOpenTx(ExitRequest req) {
        if (req.transactionId != null) {
            ParkingTransaction tx = ParkingTransaction.findById(req.transactionId);
            if (tx == null) {
                throw ApiException.notFound("Transaction not found");
            }
            if (tx.exitTime != null) {
                throw ApiException.conflict("Vehicle already exited");
            }
            return tx;
        }
        if (req.plateNumber != null && !req.plateNumber.isBlank()) {
            Vehicle v = Vehicle.findByPlate(req.plateNumber.trim().toUpperCase().replace(" ", ""));
            if (v == null) {
                throw ApiException.notFound("Vehicle not found");
            }
            ParkingTransaction tx = ParkingTransaction.openForVehicle(v.id);
            if (tx == null) {
                throw ApiException.notFound("No open session for this vehicle");
            }
            return tx;
        }
        if (req.slotNumber != null && !req.slotNumber.isBlank()) {
            ParkingSlot slot = ParkingSlot.find("slotNumber", req.slotNumber.trim().toUpperCase()).firstResult();
            if (slot == null) {
                throw ApiException.notFound("Slot not found");
            }
            ParkingTransaction tx = ParkingTransaction.openForSlot(slot.id);
            if (tx == null) {
                throw ApiException.notFound("No open session for this slot");
            }
            return tx;
        }
        throw ApiException.badRequest("Provide transactionId, plateNumber, or slotNumber");
    }
}
