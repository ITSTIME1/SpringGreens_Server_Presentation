package com.spring_greens.presentation.fcm.repository;

import com.spring_greens.presentation.fcm.dto.projection.FcmTopicNameProjection;
import com.spring_greens.presentation.fcm.entity.FcmTopic;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmTopicRepository extends CrudRepository<FcmTopic, Long> {

    @Query(value = "select fcm_topic_name from fcm_topic where shop_id = :shopId", nativeQuery = true)
    Optional<FcmTopicNameProjection> findByShopId(@Param("shopId") Long shopId);

    @Query(value = "update fcm_topic set total_subscriber = total_subscriber + 1 " +
            "where fcm_topic_Name = :fcmTopicName", nativeQuery = true)
    void increaseTotalSubscriber(@Param("fcmTopicName")String fcmTopicName);
}
