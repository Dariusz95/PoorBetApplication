package com.poorbet.users.user.service;

import com.poorbet.commons.security.PoorbetPermissions;
import com.poorbet.users.user.model.Role;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorizationPolicyService {

    private final Map<Role, List<String>> permissionsByRole = new EnumMap<>(Role.class);

    public AuthorizationPolicyService() {
        List<String> couponPermissions = List.of(PoorbetPermissions.COUPON_CREATE);
        permissionsByRole.put(Role.GUEST, couponPermissions);
        permissionsByRole.put(Role.USER, couponPermissions);
        permissionsByRole.put(Role.ADMIN, couponPermissions);
    }

    public List<String> resolvePermissions(Role role) {
        return permissionsByRole.getOrDefault(role, List.of());
    }
}
