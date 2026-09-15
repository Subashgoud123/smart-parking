package com.smartparking.resource;

import com.smartparking.dto.VehicleDto;
import com.smartparking.dto.VehicleRequest;
import com.smartparking.service.VehicleService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/vehicles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "CUSTOMER", "STAFF"})
@Tag(name = "Vehicles")
public class VehicleResource {
    @Inject
    VehicleService vehicles;

    @GET
    public List<VehicleDto> list() {
        return vehicles.list();
    }

    @GET
    @Path("/{id}")
    public VehicleDto get(@PathParam("id") Long id) {
        return vehicles.get(id);
    }

    @POST
    public VehicleDto create(@Valid VehicleRequest req) {
        return vehicles.create(req);
    }

    @PUT
    @Path("/{id}")
    public VehicleDto update(@PathParam("id") Long id, @Valid VehicleRequest req) {
        return vehicles.update(id, req);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        vehicles.delete(id);
    }
}
