package com.smartparking.resource;

import com.smartparking.dto.LoginRequest;
import com.smartparking.dto.RegisterRequest;
import com.smartparking.dto.TokenResponse;
import com.smartparking.dto.UserDto;
import com.smartparking.security.CurrentUser;
import com.smartparking.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication")
public class AuthResource {
    @Inject
    AuthService auth;
    @Inject
    CurrentUser current;

    @POST
    @Path("/register")
    @PermitAll
    public TokenResponse register(@Valid RegisterRequest req) {
        return auth.register(req);
    }

    @POST
    @Path("/login")
    @PermitAll
    public TokenResponse login(@Valid LoginRequest req) {
        return auth.login(req);
    }

    @GET
    @Path("/me")
    @RolesAllowed({"ADMIN", "CUSTOMER", "STAFF"})
    public UserDto me() {
        return auth.me(current.account());
    }
}
