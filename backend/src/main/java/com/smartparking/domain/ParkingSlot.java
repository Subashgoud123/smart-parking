package com.smartparking.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "parking_slots")
public class ParkingSlot extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "slot_number", nullable = false, unique = true, length = 32)
    public String slotNumber;

    @Column(nullable = false, length = 80)
    public String area;

    @Column(nullable = false, length = 40)
    public String floor;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 32)
    public VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    public SlotStatus status = SlotStatus.VACANT;

    @Column(length = 255)
    public String notes;
}
