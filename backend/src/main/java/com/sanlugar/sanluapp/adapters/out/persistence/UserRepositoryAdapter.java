package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sanlugar.sanluapp.domain.model.User;
import com.sanlugar.sanluapp.domain.port.UserRepository;
import com.sanlugar.sanluapp.adapters.mappers.UserMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository repo;

    @Override
    public Optional<User> findById(Long id) {
        return repo.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = repo.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public List<User> findAll() {
        return repo.findAll().stream().map(UserMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
