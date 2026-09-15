package com.smartparking.resource;

import com.smartparking.dto.DashboardStatsDto;
import com.smartparking.service.DashboardService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "STAFF"})
@Tag(name = "Dashboard")
public class DashboardResource {
    @Inject
    DashboardService dashboard;

    @GET
    @Path("/statistics")
    public DashboardStatsDto statistics() {
        return dashboard.statistics();
    }
}
