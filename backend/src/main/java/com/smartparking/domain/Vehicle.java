package com.smartparking.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "plate_number", nullable = false, unique = true, length = 32)
    public String plateNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 32)
    public VehicleType vehicleType;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    public UserAccount owner;

    @Column(name = "contact_phone", length = 40)
    public String contactPhone;

    @Column(length = 80)
    public String nickname;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    public static Vehicle findByPlate(String plate) {
        return find("plateNumber", plate.toUpperCase()).firstResult();
    }
}
