package com.smartparking.resource;

import com.smartparking.domain.UserAccount;
import com.smartparking.dto.UserDto;
import com.smartparking.mapper.DtoMapper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
@Tag(name = "Users")
public class UserResource {
    @GET
    public List<UserDto> list() {
        return UserAccount.<UserAccount>listAll().stream().map(DtoMapper::user).toList();
    }
}
