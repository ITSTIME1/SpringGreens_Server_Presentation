package com.spring_greens.presentation.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.spring_greens.presentation.fcm.dto.projection.FcmTokenProjection;
import com.spring_greens.presentation.fcm.dto.projection.FcmTopicNameProjection;
import com.spring_greens.presentation.fcm.dto.request.FcmReserveRequest;
import com.spring_greens.presentation.fcm.dto.request.FcmSaveTokenRequest;
import com.spring_greens.presentation.fcm.dto.request.FcmServiceRequest;
import com.spring_greens.presentation.fcm.dto.request.FcmSubscriptionRequest;
import com.spring_greens.presentation.fcm.entity.*;
import com.spring_greens.presentation.fcm.exception.FcmException;
import com.spring_greens.presentation.fcm.repository.*;
import com.spring_greens.presentation.fcm.validator.ifs.FcmServiceValidator;
import com.spring_greens.presentation.global.exception.CommonException;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.global.factory.service.ifs.ServiceFactory;
import com.spring_greens.presentation.shop.dto.projection.FcmServiceRequestProjection;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.PatternSyntaxException;


@Slf4j
@Service
public class FcmService {
    private final FcmServiceRequestDetailsRepository fcmServiceRequestDetailsRepository;
    private final FcmSubscriptionRepository fcmSubscriptionRepository;
    private final FcmTopicRepository fcmTopicRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmReservationMessageRepository fcmReservationMessageRepository;
    private final ConverterFactory converterFactory;
    private final ServiceFactory serviceFactory;
    private final FcmServiceValidator fcmServiceValidator;
    private final Environment environment;

    public FcmService(FcmServiceRequestDetailsRepository fcmServiceRequestDetailsRepository,
                      FcmSubscriptionRepository fcmSubscriptionRepository,
                      FcmTopicRepository fcmTopicRepository,
                      FcmTokenRepository fcmTokenRepository,
                      FcmReservationMessageRepository fcmReservationMessageRepository,
                      ConverterFactory converterFactory,
                      @Lazy ServiceFactory serviceFactory,
                      FcmServiceValidator fcmServiceValidator, Environment environment)
    {
        this.fcmServiceRequestDetailsRepository = fcmServiceRequestDetailsRepository;
        this.fcmSubscriptionRepository = fcmSubscriptionRepository;
        this.fcmTopicRepository = fcmTopicRepository;
        this.fcmTokenRepository = fcmTokenRepository;
        this.fcmReservationMessageRepository = fcmReservationMessageRepository;
        this.converterFactory = converterFactory;
        this.serviceFactory = serviceFactory;
        this.fcmServiceValidator = fcmServiceValidator;
        this.environment = environment;
    }


    @Transactional
    public boolean registerFcmService(@NotNull FcmServiceRequest fcmServiceRequest) {

        try {
            validateFcmServiceRequest(fcmServiceRequest);
        } catch (IllegalArgumentException e) {
            log.error("Error invalid argument");
            throw new FcmException.FcmIllegalArgumentException(e.getMessage());
        }


        FcmServiceRequestProjection shopDetails =
                serviceFactory.getShopService().getShopIdAndNameForFcmServiceRequestDetails(fcmServiceRequest.getMemberId());

        createAndSaveFcmServiceRequest(shopDetails.getId());

        try {

            String replaceTopicName = createFcmTopicAndSaveToFcmServer(shopDetails.getName(), fcmServiceRequest.getFcmToken());

            if(replaceTopicName.isEmpty()) {
                throw new IllegalArgumentException("TopicName is invalid argument");
            }

            createFcmTopicAndSaveToDB(shopDetails.getId(), replaceTopicName);

        } catch (FirebaseMessagingException e) {

            log.error("Error createTopic : {}", e.getMessage(), e);
            throw new FcmException.FcmCreatingTopicException("Failed creating topic");

        } catch (PatternSyntaxException e) {

            log.error("Error regex pattern : {}", e.getMessage(), e);
            throw new CommonException.CustomRegexPatternException(e.getMessage());

        } catch (IllegalArgumentException e) {

            log.error("Error topicName is null : {}", e.getMessage(), e);
            throw new CommonException.CustomIllegalArgumentException(e.getMessage());

        }

        return true;
    }

    @Transactional
    public boolean subscribeFcmTopic(@NotNull FcmSubscriptionRequest fcmSubscriptionRequest){

        try {
            validateFcmSubscriptionRequest(fcmSubscriptionRequest);
        } catch (IllegalArgumentException e) {
            throw new FcmException.FcmIllegalArgumentException(e.getMessage());
        }

        FcmTopicNameProjection fcmTopicName =
                fcmTopicRepository.findByShopId(fcmSubscriptionRequest.getShopId())
                .orElseThrow(() -> new CommonException.CustomNullPointerException("Not found fcm topicName"));

        FcmTokenProjection fcmToken =
                fcmTokenRepository.findByMemberId(fcmSubscriptionRequest.getMemberId())
                .orElseThrow(() -> new CommonException.CustomNullPointerException("Not found fcmToken"));

        try {
            requestSubscriptionToFcmServer(fcmToken.getToken(), fcmTopicName.getFcmTopicName());
            log.info("Success subscribe topic");

            FcmSubscription fcmSubscription = converterFactory.getFcmConverter().createFcmSubscription(fcmSubscriptionRequest.getMemberId(), fcmTopicName.getFcmTopicName());

            fcmSubscriptionRepository.save(fcmSubscription);

            fcmTopicRepository.increaseTotalSubscriber(fcmTopicName.getFcmTopicName());

        } catch (FirebaseMessagingException e) {
            log.error("Failed subscribe topic");
            throw new FcmException.FcmSubscriptionException("Failed subscribe to topic");
        }



        return true;
    }

    @Transactional
    public boolean registerFcmToken(@NotNull FcmSaveTokenRequest fcmSaveTokenRequest){

        try {
            validateFcmRegisterFcmToken(fcmSaveTokenRequest);
        } catch (IllegalArgumentException e) {
            throw new FcmException.FcmIllegalArgumentException(e.getMessage());
        }

        FcmToken fcmToken = converterFactory.getFcmConverter()
                .createFcmToken(fcmSaveTokenRequest.getMemberId(), fcmSaveTokenRequest.getFcmToken(), fcmSaveTokenRequest.getCreatedDateTime());

        fcmTokenRepository.save(fcmToken);
        return true;
    }

    @Transactional
    public boolean reserveFcmMessage(@NotNull FcmReserveRequest fcmReserveRequest) {
        try {
            validateFcmReserveMessage(fcmReserveRequest);
        } catch (NullPointerException e) {
            throw new CommonException.CustomNullPointerException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new FcmException.FcmIllegalArgumentException(e.getMessage());
        }

        FcmTopicNameProjection fcmTopicName = fcmTopicRepository.findByShopId(fcmReserveRequest.getShopId())

                .orElseThrow(() -> new CommonException.CustomNullPointerException("Not found fcmTopicName"));


        String imageFilePath = createImageDirAndPath(fcmReserveRequest);

        FcmReservationMessage fcmReserveMessage = converterFactory.getFcmConverter().createFcmReservationMessage(fcmReserveRequest, imageFilePath, fcmTopicName.getFcmTopicName());

        fcmReservationMessageRepository.save(fcmReserveMessage);

        return true;
    }

    private String createImageDirAndPath(FcmReserveRequest fcmReserveRequest) {

        // 1. 이미지가 중복되지 않도록 UUID 랜덤값을 사용해 이미지 이름을 만듦.
        String serverImageFileName = UUID.randomUUID().toString()
                .concat("_")
                .concat(Objects.requireNonNull(fcmReserveRequest.getImage().getOriginalFilename()));


        // 2. 절대경로상에 해당 폴더가 있는지 확인하기 위해서 환경변수로 지정한 서버 파일절대경로를 가지고옴.
        MultipartFile imageFile = fcmReserveRequest.getImage();
        Path absolutePath = Paths.get(Objects.requireNonNull(Objects.requireNonNull(environment.getProperty("file.upload.absolute.path")).concat("reserveMessage"))).toAbsolutePath();

        log.info(absolutePath.toString());

        // 3. 절대경로상에 예약 메세지를 임시저장할 폴더가 존재하지 않는다면 폴더를 생성함.
        if (Files.notExists(absolutePath)) {
            try {
                Files.createDirectories(absolutePath);
            } catch (IOException e) {
                throw new FcmException.FcmIOException(e.getMessage());
            }
        }

        // 4. 절대경로상에 파일이 존재하거나, 없어서 생성한 이후 절대경로와 + 서버에 저장할 중복되지 않는 이미지 이름을 File 객체로 만듦.
        File targetFile = new File(absolutePath.toFile().toString(), serverImageFileName);

        // 5. 실제 이미지 파일을 서버 파일시스템에 저장.
        try {
            imageFile.transferTo(targetFile);
        } catch (IOException e) {
            log.error("Error imageFile writes : {}", e.getMessage(), e);
            throw new FcmException.FcmIOException(e.getMessage());
        }

        // 6. 클라이언트에서 파일시스템에 접근하여 이미지를 활용할 수 있도록 접근 가능한 URL 경로를 따로 만들어서 DB에 저장후 Fcm 메세지에 활용.
        return "/images/reserveMessage/" + serverImageFileName;
    }

    private void requestSubscriptionToFcmServer(String fcmToken, String fcmTopicName) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().subscribeToTopic(List.of(fcmToken), fcmTopicName);
    }

    private String createFcmTopicAndSaveToFcmServer(String shopName, String fcmToken) throws FirebaseMessagingException, PatternSyntaxException {
        String replaceTopicName = shopName.toLowerCase().replaceAll("\\s+", "");
        log.info("Topic Name : {}", replaceTopicName);
        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(List.of(fcmToken), replaceTopicName);
        log.info("Fcm response : {}", response.getErrors());

        return replaceTopicName;
    }
    private void createFcmTopicAndSaveToDB(Long shopId, String topicName) {

        FcmTopic fcmTopic =
                converterFactory.getFcmConverter().createFcmTopic(shopId, topicName);

        fcmTopicRepository.save(fcmTopic);
    }

    private void createAndSaveFcmServiceRequest(Long shopId) {

        FcmServiceRequestDetails fcmServiceRequestDetails =
                converterFactory.getFcmConverter().createFcmServiceRequestDetails(shopId);

        fcmServiceRequestDetailsRepository.save(fcmServiceRequestDetails);
    }

    /**
     * This method will validate the 'FcmServiceRequest' by FcmServiceValidator.
     * @param fcmServiceRequest
     * @throws IllegalArgumentException
     */

    private void validateFcmServiceRequest(@NotNull FcmServiceRequest fcmServiceRequest) throws IllegalArgumentException{
        fcmServiceValidator.validate(fcmServiceRequest);
    }

    /**
     * This method will validate the 'FcmServiceRequest' by FcmServiceValidator.
     * @param fcmSubscriptionRequest
     * @throws IllegalArgumentException
     */
    private void validateFcmSubscriptionRequest(@NotNull FcmSubscriptionRequest fcmSubscriptionRequest) throws IllegalArgumentException{
        fcmServiceValidator.validate(fcmSubscriptionRequest);
    }


    /**
     * This method will validate the 'FcmRegisterFcmToken' by FcmServiceValidator.
     * @param fcmSaveTokenRequest
     * @throws IllegalArgumentException
     */

    private void validateFcmRegisterFcmToken(@NotNull FcmSaveTokenRequest fcmSaveTokenRequest) throws IllegalArgumentException{
        fcmServiceValidator.validate(fcmSaveTokenRequest);
    }

    /**
     * This method will validate 'FcmReserveMessage' by FcmServiceRequest.
     * @param fcmReserveRequest
     */
    private void validateFcmReserveMessage(@NotNull FcmReserveRequest fcmReserveRequest) throws IllegalArgumentException, NullPointerException{
        fcmServiceValidator.validate(fcmReserveRequest);
    }
}
