package com.sanlugar.sanluapp.domain.port;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.Role;

public interface RoleRepository {
    Optional<Role> findByName(String name);
    List<Role> findAll();
}
