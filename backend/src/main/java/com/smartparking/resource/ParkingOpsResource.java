package com.smartparking.resource;

import com.smartparking.dto.EntryRequest;
import com.smartparking.dto.ExitRequest;
import com.smartparking.dto.TransactionDto;
import com.smartparking.service.GateService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/parking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "STAFF"})
@Tag(name = "Entry and exit")
public class ParkingOpsResource {
    @Inject
    GateService gate;

    @POST
    @Path("/entry")
    public TransactionDto entry(EntryRequest req) {
        return gate.entry(req);
    }

    @POST
    @Path("/exit")
    public TransactionDto exit(ExitRequest req) {
        return gate.exit(req);
    }

    @GET
    @Path("/sessions")
    public List<TransactionDto> sessions() {
        return gate.openSessions();
    }
}
