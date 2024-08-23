package com.spring_greens.presentation.auth.service;

import com.spring_greens.presentation.auth.dto.request.RetailSignupRequest;
import com.spring_greens.presentation.auth.dto.request.WholesaleSignupRequest;
import com.spring_greens.presentation.global.enums.Role;
import com.spring_greens.presentation.shop.entity.Shop;
import com.spring_greens.presentation.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.spring_greens.presentation.auth.dto.CustomUser;
import com.spring_greens.presentation.auth.dto.UserDTO;
import com.spring_greens.presentation.auth.entity.User;
import com.spring_greens.presentation.auth.repository.UserRepository;

import jakarta.transaction.Transactional;

@Slf4j
@Service
public class UserService implements UserDetailsService{

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ShopRepository shopRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails securityUser = createUserDetails(userRepository.findByEmail(email).orElseThrow(()->{
            return new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다.: "+ email);
        }));
        return securityUser;
    }

    private UserDetails createUserDetails(User user){
        CustomUser customUser = new CustomUser(
                UserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .role(user.getRole())
                        .build());

        return customUser;
    }

    @Transactional
    public boolean retailRegister(RetailSignupRequest retailSignupRequest){

        if(userRepository.findByEmail(retailSignupRequest.getEmail()).isEmpty()){
            log.info("소매 회원가입 진행");
            User user = User.builder()
                    .role(Role.ROLE_RETAILER)
                    .email(retailSignupRequest.getEmail())
                    .password(passwordEncoder.encode(retailSignupRequest.getPassword()))
                    .contact(retailSignupRequest.getContact())
                    .businessNumber(retailSignupRequest.getBusinessNumber())
                    .name(retailSignupRequest.getName())
                    .roadAddress(retailSignupRequest.getRoadAddress())
                    .addressDetails(retailSignupRequest.getAddressDetails())
                    .build();

            userRepository.save(user);
            return true;
        }else{
            System.out.println(userRepository.findByEmail(retailSignupRequest.getEmail()).get());
            log.info("소매 회원가입 이메일 존재.");
            throw new IllegalArgumentException("이미 사용 중인 사용자 이메일입니다.");
        }
    }

    @Transactional
    public boolean wholesaleRegister(WholesaleSignupRequest wholesaleSignupRequest){
        if(userRepository.findByEmail(wholesaleSignupRequest.getEmail()).isEmpty()){
            log.info("도매 회원가입 진행");
            User user = User.builder()
                    .role(Role.ROLE_WHOLESALER)
                    .email(wholesaleSignupRequest.getEmail())
                    .password(passwordEncoder.encode(wholesaleSignupRequest.getPassword()))
                    .contact(wholesaleSignupRequest.getContact())
                    .businessNumber(wholesaleSignupRequest.getBusinessNumber())
                    .name(wholesaleSignupRequest.getName())
                    .roadAddress(wholesaleSignupRequest.getRoadAddress())
                    .addressDetails(wholesaleSignupRequest.getAddressDetails())
                    .build();
            log.info("유저 저장");
            User savedUser = userRepository.save(user);
            log.info("유저 저장 완료");
            Shop shop = Shop.builder()
                    .userId(savedUser.getId())
                    .contact(wholesaleSignupRequest.getShopContact())
                    .name(wholesaleSignupRequest.getShopName())
                    .intro(wholesaleSignupRequest.getIntro())
                    .profileType(wholesaleSignupRequest.isProfileType())
                    .roadAddress(wholesaleSignupRequest.getShopRoadAddress())
                    .addressDetails(wholesaleSignupRequest.getShopAddressDetail())
                    .startTime(wholesaleSignupRequest.getStartTime())
                    .endTime(wholesaleSignupRequest.getEndTime())
                    .build();
            log.info("가게 저장");
            shopRepository.save(shop);

            return true;
        }else{
            log.info("도매 회원가입 데이터 존재.");
            throw new IllegalArgumentException("이미 사용 중인 이메일 or 가게입니다.");
        }
    }

}
