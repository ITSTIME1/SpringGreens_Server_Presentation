package com.spring_greens.presentation.fcm.repository;

import com.spring_greens.presentation.fcm.dto.projection.FcmTokenProjection;
import com.spring_greens.presentation.fcm.entity.FcmToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmTokenRepository extends CrudRepository<FcmToken, Long> {
    @Query(value = "select token from fcm_token where user_id = :userId", nativeQuery = true)
    Optional<FcmTokenProjection> findByMemberId(@Param("userId") Long userId);
}
