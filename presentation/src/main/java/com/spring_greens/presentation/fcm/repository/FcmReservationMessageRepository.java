package com.spring_greens.presentation.fcm.repository;

import com.spring_greens.presentation.fcm.entity.FcmReservationMessage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FcmReservationMessageRepository extends CrudRepository<FcmReservationMessage, Long> {

    @Modifying
    @Query(value = "insert into fcm_reservation_message_apm (user_id, title, body, reserve_date_time, image_path, topic_name) " +
            "values (:userId, :title, :body, :reserveDateTime, :imagePath, :topicName)",
            nativeQuery = true)
    void saveToApm(
            @Param("userId") Long userId,
            @Param("title") String title,
                   @Param("body") String body,
                   @Param("reserveDateTime")
                   LocalDateTime reserveDateTime,
                   @Param("imagePath") String imagePath,
                   @Param("topicName") String topicName);



    @Modifying
    @Query(value = "insert into fcm_reservation_message_chung (user_id, title, body, reserve_date_time, image_path, topic_name) " +
            "values (:userId, :title, :body, :reserveDateTime, :imagePath, :topicName)",
            nativeQuery = true)
    void saveToChung(
            @Param("userId") Long userId,
            @Param("title") String title,
                     @Param("body") String body,
                     @Param("reserveDateTime")
                     LocalDateTime reserveDateTime,
                     @Param("imagePath") String imagePath,
                     @Param("topicName") String topicName);

    @Modifying
    @Query(value = "insert into fcm_reservation_message_dong (user_id, title, body, reserve_date_time, image_path, topic_name) " +
            "values (:userId, :title, :body, :reserveDateTime, :imagePath, :topicName)",
            nativeQuery = true)
    void saveToDong(
            @Param("userId") Long userId,
            @Param("title") String title,
                    @Param("body") String body,
                    @Param("reserveDateTime")
                    LocalDateTime reserveDateTime,
                    @Param("imagePath") String imagePath,
                    @Param("topicName") String topicName);


    @Modifying
    @Query(value = "insert into fcm_reservation_message_jeil (user_id, title, body, reserve_date_time, image_path, topic_name) " +
            "values (:userId, :title, :body, :reserveDateTime, :imagePath, :topicName)",
            nativeQuery = true)

    void saveToJeil(
            @Param("userId") Long userId,
            @Param("title") String title,
                    @Param("body") String body,
                    @Param("reserveDateTime")
                    LocalDateTime reserveDateTime,
                    @Param("imagePath") String imagePath,
                    @Param("topicName") String topicName);
}
