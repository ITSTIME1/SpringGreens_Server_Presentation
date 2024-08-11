package com.spring_greens.presentation.shop.service;

import com.spring_greens.presentation.shop.dto.projection.FcmServiceRequestProjection;
import com.spring_greens.presentation.shop.exception.ShopException;
import com.spring_greens.presentation.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    public FcmServiceRequestProjection getShopIdAndNameForFcmServiceRequestDetails (Long memberId) {
        return shopRepository.findByMemberId(memberId).orElseThrow(()->
                new ShopException.ShopDataNotFoundException("Not found data"));
    }
}
