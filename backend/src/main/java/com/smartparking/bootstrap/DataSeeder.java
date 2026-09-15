package com.smartparking.bootstrap;

import com.smartparking.domain.*;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DataSeeder {
    private static final Logger LOG = Logger.getLogger(DataSeeder.class);

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (Role.count() == 0) {
            seedRole("ADMIN");
            seedRole("CUSTOMER");
            seedRole("STAFF");
        }
        if (UserAccount.count() > 0) {
            return;
        }
        UserAccount admin = user("admin@smartparking.local", "Admin@123", "Ada Admin", "ADMIN");
        UserAccount staff = user("staff@smartparking.local", "Staff@123", "Sam Staff", "STAFF");
        UserAccount customer = user("customer@smartparking.local", "Customer@123", "Casey Customer", "CUSTOMER");

        vehicle(customer, "KA01AB1234", VehicleType.CAR, "Family sedan");
        vehicle(customer, "KA01BK2222", VehicleType.BIKE, "Commuter bike");
        vehicle(customer, "KA01EV9999", VehicleType.EV, "Office EV");

        seedSlots("C", "Car Park A", "L1", VehicleType.CAR, 12);
        seedSlots("B", "Bike Park A", "L1", VehicleType.BIKE, 8);
        seedSlots("E", "EV Bay", "L2", VehicleType.EV, 4);
        seedSlots("O", "Overflow", "L0", VehicleType.OTHER, 4);

        ParkingSlot occupied = ParkingSlot.find("slotNumber", "C02").firstResult();
        if (occupied != null) {
            occupied.status = SlotStatus.OCCUPIED;
        }
        ParkingSlot reserved = ParkingSlot.find("slotNumber", "C04").firstResult();
        if (reserved != null) {
            reserved.status = SlotStatus.RESERVED;
        }
        ParkingSlot pre = ParkingSlot.find("slotNumber", "B02").firstResult();
        if (pre != null) {
            pre.status = SlotStatus.PRE_BOOKED;
        }
        ParkingSlot oos = ParkingSlot.find("slotNumber", "O04").firstResult();
        if (oos != null) {
            oos.status = SlotStatus.OUT_OF_SERVICE;
        }

        LOG.infof("Seeded demo users %s / %s / %s", admin.email, staff.email, customer.email);
    }

    private void seedRole(String name) {
        Role r = new Role();
        r.name = name;
        r.persist();
    }

    private UserAccount user(String email, String password, String name, String roleName) {
        UserAccount u = new UserAccount();
        u.email = email;
        u.passwordHash = BcryptUtil.bcryptHash(password);
        u.fullName = name;
        u.phone = "9990001111";
        u.roles.add(Role.byName(roleName));
        u.persist();
        return u;
    }

    private void vehicle(UserAccount owner, String plate, VehicleType type, String nick) {
        Vehicle v = new Vehicle();
        v.owner = owner;
        v.plateNumber = plate;
        v.vehicleType = type;
        v.nickname = nick;
        v.contactPhone = owner.phone;
        v.persist();
    }

    private void seedSlots(String prefix, String area, String floor, VehicleType type, int count) {
        for (int i = 1; i <= count; i++) {
            ParkingSlot s = new ParkingSlot();
            s.slotNumber = prefix + String.format("%02d", i);
            s.area = area;
            s.floor = floor;
            s.vehicleType = type;
            s.status = SlotStatus.VACANT;
            s.persist();
        }
    }
}
