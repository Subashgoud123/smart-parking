package com.smartparking.security;

import com.smartparking.domain.UserAccount;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;

@ApplicationScoped
public class TokenService {
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    public String issue(UserAccount user) {
        return Jwt.issuer(issuer)
                .upn(user.email)
                .subject(String.valueOf(user.id))
                .groups(user.roleNames())
                .claim("fullName", user.fullName)
                .expiresIn(Duration.ofHours(8))
                .sign();
    }
}
