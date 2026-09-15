package com.smartparking.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_transactions")
public class ParkingTransaction extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id")
    public ParkingBooking booking;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "slot_id")
    public ParkingSlot slot;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    public Vehicle vehicle;

    @Column(name = "entry_time", nullable = false)
    public LocalDateTime entryTime;

    @Column(name = "exit_time")
    public LocalDateTime exitTime;

    @Column(name = "duration_minutes")
    public Long durationMinutes;

    public static ParkingTransaction openForVehicle(Long vehicleId) {
        return find("vehicle.id = ?1 and exitTime is null", vehicleId).firstResult();
    }

    public static ParkingTransaction openForSlot(Long slotId) {
        return find("slot.id = ?1 and exitTime is null", slotId).firstResult();
    }
}
