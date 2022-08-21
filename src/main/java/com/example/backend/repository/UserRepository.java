package com.example.backend.repository;

import com.example.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    Optional<List<User>> findByRoles(Role role);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByRolesAndEmail(Role role, String email);

    Boolean existsByUsernameAndRoles(String username,Role role);

    Boolean existsByPassword(String password);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByOffersId(Integer offer_id);

    Optional<User> findByUsernameAndPhoneNumber(String username, String phoneNumber);

    Optional<List<User>> findByCategoryIdAndGovernmentAndCity(Integer categoryId , String government , String city);

    Optional <List<User>> findByCategoryIdAndGovernment(Integer categoryId , String government);

    Optional<List<User>> findByCategoryId(Integer categoryId);

    List<User> findAll();

    Optional <List<User>> findByPhoneToken(String phoneToken);


    @Transactional
    void deleteUserByUsername(String username);
}

