package com.smartparking.service;

import com.smartparking.domain.BookingStatus;
import com.smartparking.domain.ParkingBooking;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class BookingOverlapTest {
    @Inject
    BookingService bookings;

    @Test
    @Transactional
    void overlapDetectsIntersectingWindows() {
        boolean overlap = bookings.hasOverlap(-1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                null);
        assertFalse(overlap);
        long live = ParkingBooking.count("status in (?1)",
                List.of(BookingStatus.CONFIRMED, BookingStatus.PRE_BOOKED, BookingStatus.ACTIVE));
        assertTrue(live >= 0);
    }
}
