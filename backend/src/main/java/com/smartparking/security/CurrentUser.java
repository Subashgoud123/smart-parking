package com.smartparking.security;

import com.smartparking.domain.UserAccount;
import com.smartparking.domain.UserRoleName;
import com.smartparking.exception.ApiException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class CurrentUser {
    @Inject
    JsonWebToken jwt;

    public Long id() {
        try {
            return Long.parseLong(jwt.getSubject());
        } catch (Exception e) {
            throw ApiException.unauthorized("Invalid token");
        }
    }

    public String email() {
        return jwt.getName();
    }

    public UserAccount account() {
        UserAccount user = UserAccount.findById(id());
        if (user == null || !user.active) {
            throw ApiException.unauthorized("Account unavailable");
        }
        return user;
    }

    public boolean isAdmin() {
        return jwt.getGroups().contains(UserRoleName.ADMIN.name());
    }

    public boolean isStaff() {
        return jwt.getGroups().contains(UserRoleName.STAFF.name()) || isAdmin();
    }

    public void requireOwnerOrStaff(Long ownerId) {
        if (!isStaff() && !id().equals(ownerId)) {
            throw ApiException.forbidden("Not allowed for this resource");
        }
    }
}
