package com.riwi.Hospital.domain.enums;

import java.util.Collections;
import java.util.Set;

public enum Role {

    ADMIN(Set.of("MANAGE_ALL")),
    DOCTOR(Set.of("MANAGE_APPOINTMENT")),
    PATIENT(Set.of("VIEW_APPOINTMENT"));

    private final Set<String> permissions;

    Role(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }
}