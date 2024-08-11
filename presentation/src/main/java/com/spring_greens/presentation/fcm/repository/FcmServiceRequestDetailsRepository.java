package com.spring_greens.presentation.fcm.repository;

import com.spring_greens.presentation.fcm.entity.FcmServiceRequestDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmServiceRequestDetailsRepository extends CrudRepository<FcmServiceRequestDetails, Long> {
}
