package com.smartparking.mapper;

import com.smartparking.domain.*;
import com.smartparking.dto.*;

public final class DtoMapper {
    private DtoMapper() {}

    public static UserDto user(UserAccount u) {
        UserDto d = new UserDto();
        d.id = u.id;
        d.email = u.email;
        d.fullName = u.fullName;
        d.phone = u.phone;
        d.active = u.active;
        d.roles = u.roleNames();
        return d;
    }

    public static VehicleDto vehicle(Vehicle v) {
        VehicleDto d = new VehicleDto();
        d.id = v.id;
        d.plateNumber = v.plateNumber;
        d.vehicleType = v.vehicleType;
        d.ownerId = v.owner.id;
        d.ownerName = v.owner.fullName;
        d.contactPhone = v.contactPhone;
        d.nickname = v.nickname;
        return d;
    }

    public static SlotDto slot(ParkingSlot s) {
        SlotDto d = new SlotDto();
        d.id = s.id;
        d.slotNumber = s.slotNumber;
        d.area = s.area;
        d.floor = s.floor;
        d.vehicleType = s.vehicleType;
        d.status = s.status;
        d.notes = s.notes;
        return d;
    }

    public static BookingDto booking(ParkingBooking b) {
        BookingDto d = new BookingDto();
        d.id = b.id;
        d.userId = b.user.id;
        d.userName = b.user.fullName;
        d.vehicleId = b.vehicle.id;
        d.plateNumber = b.vehicle.plateNumber;
        d.slotId = b.slot.id;
        d.slotNumber = b.slot.slotNumber;
        d.startAt = b.startAt;
        d.endAt = b.endAt;
        d.status = b.status;
        d.bulkGroupId = b.bulkGroupId;
        return d;
    }

    public static TransactionDto tx(ParkingTransaction t) {
        TransactionDto d = new TransactionDto();
        d.id = t.id;
        d.bookingId = t.booking == null ? null : t.booking.id;
        d.slotId = t.slot.id;
        d.slotNumber = t.slot.slotNumber;
        d.vehicleId = t.vehicle.id;
        d.plateNumber = t.vehicle.plateNumber;
        d.entryTime = t.entryTime;
        d.exitTime = t.exitTime;
        d.durationMinutes = t.durationMinutes;
        return d;
    }
}
