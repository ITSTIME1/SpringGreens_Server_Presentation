package com.spring_greens.presentation.mall.service;

import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.mall.dto.projection.Coordinate;
import com.spring_greens.presentation.mall.dto.projection.Destination;
import com.spring_greens.presentation.mall.dto.request.MallRequest;
import com.spring_greens.presentation.mall.dto.response.MallDestinationResponse;
import com.spring_greens.presentation.mall.dto.response.MallStreetResponse;
import com.spring_greens.presentation.mall.exception.MallException;
import com.spring_greens.presentation.mall.repository.MallRepository;
import com.spring_greens.presentation.mall.service.ifs.MallService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * MallService performs business logic related to shopping malls.
 * @author itstime0809
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MallServiceImpl implements MallService {
    private final MallRepository mallRepository;
    private final ConverterFactory converterFactory;

    @Override
    @Transactional(readOnly = true)
    public MallStreetResponse getMallStreetInformation() {
        // 1. Get mall names
        List<String> mallNameList = mallRepository.findAllMallName()
                .orElseThrow(() -> {
                    log.error("Mall names not found");
                    return new MallException.MallEntityNotFoundException("Not Found mall names");
                });

        // 2. Get coordinates
        Coordinate coordinate = mallRepository.findStandardCoordinate()
                .orElseThrow(() -> {
                    log.error("Standard coordinate not found");
                    return new MallException.MallEntityNotFoundException("Not Found standard coordinate");
                });

        return converterFactory.getMallConverter().createMallStreetResponse(coordinate, mallNameList);
    }


    @Override
    @Transactional(readOnly = true)
    public MallDestinationResponse getMallDestinationInformation(@NotNull MallRequest mallRequest) {

        // 1. Get specific mall information that contained width, latitude, longitude
        Destination destination = mallRepository.findMallDestination(mallRequest.getName())
                .orElseThrow(() -> {
                    log.error("Destination Information not found");
                    return new MallException.MallEntityNotFoundException("Not Found destination information");
                });

        return converterFactory.getMallConverter().createMallDestinationResponse(destination);
    }
}
