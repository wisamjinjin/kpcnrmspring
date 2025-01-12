package com.jinjin.bidsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jinjin.bidsystem.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Boolean existsByUsername(String username);
    
    UserEntity findByUsername(String username);
    
    UserEntity findByTelno(String telno);

    Boolean existsByTelno(String telno);

    Boolean existsByEmail(String email);
}
