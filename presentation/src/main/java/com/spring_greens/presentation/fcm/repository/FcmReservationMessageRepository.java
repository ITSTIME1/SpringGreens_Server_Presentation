package com.spring_greens.presentation.fcm.repository;

import com.spring_greens.presentation.fcm.entity.FcmReservationMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmReservationMessageRepository extends CrudRepository<FcmReservationMessage, Long> {
}
