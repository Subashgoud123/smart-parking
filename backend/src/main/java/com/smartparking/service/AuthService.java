package com.smartparking.service;

import com.smartparking.domain.Role;
import com.smartparking.domain.UserAccount;
import com.smartparking.domain.UserRoleName;
import com.smartparking.dto.LoginRequest;
import com.smartparking.dto.RegisterRequest;
import com.smartparking.dto.TokenResponse;
import com.smartparking.dto.UserDto;
import com.smartparking.exception.ApiException;
import com.smartparking.mapper.DtoMapper;
import com.smartparking.security.TokenService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AuthService {
    private static final Logger LOG = Logger.getLogger(AuthService.class);

    @Inject
    TokenService tokens;

    @Transactional
    public TokenResponse register(RegisterRequest req) {
        if (UserAccount.findByEmail(req.email.toLowerCase()) != null) {
            throw ApiException.conflict("Email already registered");
        }
        UserAccount user = new UserAccount();
        user.email = req.email.toLowerCase().trim();
        user.passwordHash = BcryptUtil.bcryptHash(req.password);
        user.fullName = req.fullName.trim();
        user.phone = req.phone;
        Role customer = Role.byName(UserRoleName.CUSTOMER.name());
        if (customer == null) {
            throw ApiException.badRequest("Roles are not seeded");
        }
        user.roles.add(customer);
        user.persist();
        LOG.infof("Registered user %s", user.email);
        return tokenFor(user);
    }

    public TokenResponse login(LoginRequest req) {
        UserAccount user = UserAccount.findByEmail(req.email.toLowerCase().trim());
        if (user == null || !user.active || !BcryptUtil.matches(req.password, user.passwordHash)) {
            throw ApiException.unauthorized("Invalid email or password");
        }
        return tokenFor(user);
    }

    public UserDto me(UserAccount user) {
        return DtoMapper.user(user);
    }

    private TokenResponse tokenFor(UserAccount user) {
        TokenResponse res = new TokenResponse();
        res.token = tokens.issue(user);
        res.user = DtoMapper.user(user);
        return res;
    }
}
