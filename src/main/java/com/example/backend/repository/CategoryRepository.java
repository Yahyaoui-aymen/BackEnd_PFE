package com.example.backend.repository;

import com.example.backend.entity.Category;
import com.example.backend.entity.ECategory;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName (ECategory categoryName);
}
