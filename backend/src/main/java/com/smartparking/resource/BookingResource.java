package com.smartparking.resource;

import com.smartparking.dto.BookingDto;
import com.smartparking.dto.BookingRequest;
import com.smartparking.dto.BulkBookingRequest;
import com.smartparking.service.BookingService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "CUSTOMER", "STAFF"})
@Tag(name = "Bookings")
public class BookingResource {
    @Inject
    BookingService bookings;

    @GET
    public List<BookingDto> list() {
        return bookings.list();
    }

    @GET
    @Path("/{id}")
    public BookingDto get(@PathParam("id") Long id) {
        return bookings.get(id);
    }

    @POST
    public BookingDto create(@Valid BookingRequest req) {
        return bookings.create(req);
    }

    @POST
    @Path("/bulk")
    public List<BookingDto> bulk(@Valid BulkBookingRequest req) {
        return bookings.bulk(req);
    }

    @PUT
    @Path("/{id}/cancel")
    public BookingDto cancel(@PathParam("id") Long id) {
        return bookings.cancel(id);
    }
}
