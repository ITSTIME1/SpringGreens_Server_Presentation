package com.spring_greens.presentation.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring_greens.presentation.auth.entity.Shop;
import com.spring_greens.presentation.auth.entity.User;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long>{
    Optional<Shop> findByname(String name);    
}
