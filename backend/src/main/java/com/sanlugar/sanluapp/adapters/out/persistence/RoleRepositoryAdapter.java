package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sanlugar.sanluapp.adapters.mappers.RoleMapper;
import com.sanlugar.sanluapp.domain.model.Role;
import com.sanlugar.sanluapp.domain.port.RoleRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final SpringDataRoleRepository repo;

    @Override
    public Optional<Role> findByName(String name) {
        if (name == null) return Optional.empty();
        return repo.findByNameIgnoreCase(name).map(RoleMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return repo.findAllByOrderByNameAsc().stream()
                .map(RoleMapper::toDomain)
                .collect(Collectors.toList());
    }
}
