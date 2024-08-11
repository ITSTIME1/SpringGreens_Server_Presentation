package com.spring_greens.presentation.fcm.repository;
import com.spring_greens.presentation.fcm.entity.FcmSubscription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmSubscriptionRepository extends CrudRepository<FcmSubscription, Long> {
}
