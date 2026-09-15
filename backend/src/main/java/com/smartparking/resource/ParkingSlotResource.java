package com.smartparking.resource;

import com.smartparking.domain.VehicleType;
import com.smartparking.dto.SlotDetailDto;
import com.smartparking.dto.SlotDto;
import com.smartparking.dto.SlotRequest;
import com.smartparking.service.DashboardService;
import com.smartparking.service.SlotService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/parking-slots")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "CUSTOMER", "STAFF"})
@Tag(name = "Parking slots")
public class ParkingSlotResource {
    @Inject
    SlotService slots;
    @Inject
    DashboardService dashboard;

    @GET
    public List<SlotDto> list() {
        return slots.list();
    }

    @GET
    @Path("/available")
    public List<SlotDto> available(@QueryParam("type") VehicleType type) {
        return slots.available(type);
    }

    @GET
    @Path("/{id}")
    public SlotDto get(@PathParam("id") Long id) {
        return slots.get(id);
    }

    @GET
    @Path("/{id}/detail")
    public SlotDetailDto detail(@PathParam("id") Long id) {
        return dashboard.slotDetail(id);
    }

    @POST
    @RolesAllowed("ADMIN")
    public SlotDto create(@Valid SlotRequest req) {
        return slots.create(req);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public SlotDto update(@PathParam("id") Long id, @Valid SlotRequest req) {
        return slots.update(id, req);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public void delete(@PathParam("id") Long id) {
        slots.delete(id);
    }
}
