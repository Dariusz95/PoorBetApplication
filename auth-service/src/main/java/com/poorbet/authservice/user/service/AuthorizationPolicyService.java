package com.poorbet.authservice.user.service;

import com.poorbet.authservice.user.model.Role;
import com.poorbet.authstarter.security.PoorbetPermissions;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorizationPolicyService {

    private final Map<Role, List<String>> permissionsByRole = new EnumMap<>(Role.class);

    public AuthorizationPolicyService() {
        permissionsByRole.put(Role.GUEST, List.of(PoorbetPermissions.COUPON_CREATE));
        permissionsByRole.put(Role.USER,
                List.of(
                        PoorbetPermissions.COUPON_CREATE,
                        PoorbetPermissions.TEAM_CREATE,
                        PoorbetPermissions.TEAM_UPDATE,
                        PoorbetPermissions.TEAM_READ
                ));
        permissionsByRole.put(Role.ADMIN, List.of(PoorbetPermissions.COUPON_CREATE));
    }

    public List<String> resolvePermissions(Role role) {
        return permissionsByRole.getOrDefault(role, List.of());
    }
}
