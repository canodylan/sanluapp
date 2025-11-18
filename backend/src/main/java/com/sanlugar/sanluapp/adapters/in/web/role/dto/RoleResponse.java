package com.sanlugar.sanluapp.adapters.in.web.role.dto;

import com.sanlugar.sanluapp.domain.model.Role;

public record RoleResponse(Long id, String name, String displayName) {

    public static RoleResponse from(Role role) {
        if (role == null) {
            return new RoleResponse(null, null, null);
        }
        return new RoleResponse(role.getId(), role.getName(), humanize(role.getName()));
    }

    private static String humanize(String name) {
        if (name == null || name.isBlank()) return "";
        String cleaned = name.trim().replace("ROLE_", "");
        String[] parts = cleaned.split("[_ ]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isBlank()) continue;
            if (builder.length() > 0) builder.append(' ');
            builder.append(part.substring(0, 1).toUpperCase())
                    .append(part.substring(1).toLowerCase());
        }
        return builder.toString();
    }
}
