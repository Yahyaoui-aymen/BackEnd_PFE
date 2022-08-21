package com.example.backend.repository;

import com.example.backend.entity.PhoneToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhoneTokenRepository extends JpaRepository<PhoneToken, Long> {

    Optional<List<PhoneToken>> findByUser_Id (Long userId) ;

    Boolean existsByToken(String token);

    Optional <PhoneToken> findByToken(String token) ;

    void deleteById(Long id);
}
