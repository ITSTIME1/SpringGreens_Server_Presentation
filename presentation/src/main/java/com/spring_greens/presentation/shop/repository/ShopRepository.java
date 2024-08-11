package com.spring_greens.presentation.shop.repository;

import com.spring_greens.presentation.shop.dto.projection.FcmServiceRequestProjection;
import com.spring_greens.presentation.shop.entity.Shop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends CrudRepository<Shop, Long> {

    @Query(value = "select p.id as id, p.name as name from shop p " +
            "where p.member_id = :memberId", nativeQuery = true)
    Optional<FcmServiceRequestProjection> findByMemberId(@Param("memberId") Long memberId);
}
