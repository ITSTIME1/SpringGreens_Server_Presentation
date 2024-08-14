package com.spring_greens.presentation.auth.service;

import java.net.http.HttpRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import com.spring_greens.presentation.auth.dto.CustomUser;
import com.spring_greens.presentation.auth.dto.LoginDTO;
import com.spring_greens.presentation.auth.dto.RetailSignupDTO;
import com.spring_greens.presentation.auth.dto.UserDTO;
import com.spring_greens.presentation.auth.dto.WholesaleSignupDTO;
import com.spring_greens.presentation.auth.entity.Shop;
import com.spring_greens.presentation.auth.entity.User;
import com.spring_greens.presentation.auth.repository.ShopRepository;
import com.spring_greens.presentation.auth.repository.UserRepository;
import com.spring_greens.presentation.auth.security.handler.CustomSuccessHandler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService{

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    // private final AuthenticationManagerBuilder authenticationManagerBuilder;
    // private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // return userRepository.findByEmail(email)
        // .map(createUserDetails(createLoginDTO(this)))
        // .orElseThrow(() -> new UsernameNotFoundException("해당 회원은 없습니다."));
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
    public void retailRegister(RetailSignupDTO retailSignupDTO){

        if(userRepository.findByEmail(retailSignupDTO.getEmail()).isEmpty()){
            logger.info("소매 회원가입 진행");
            User user = User.builder()
                            .role(retailSignupDTO.getRole())
                            .email(retailSignupDTO.getEmail())
                            // .password(bCryptPasswordEncoder.encode(retailSignupDTO.getPassword()))\
                            .password(retailSignupDTO.getPassword())
                            .contact(retailSignupDTO.getContact())
                            .businessNumber(retailSignupDTO.getBusinessNumber())
                            .name(retailSignupDTO.getName())
                            .alertType(retailSignupDTO.isAlertType())
                            .termsType(retailSignupDTO.isTermsType())
                            .socialType(retailSignupDTO.isSocialType())
                            .socialName(retailSignupDTO.getSocialName())
                            .build();              

            userRepository.save(user);
        }else{
            System.out.println(userRepository.findByEmail(retailSignupDTO.getEmail()).get());
            logger.info("소매 회원가입 이메일 존재.");
            throw new IllegalArgumentException("이미 사용 중인 사용자 이메일입니다.");
        }        
    }

    @Transactional
    public void wholesaleRegister(WholesaleSignupDTO wholesaleSignupDTO){
        if(userRepository.findByEmail(wholesaleSignupDTO.getEmail()).isEmpty()&&shopRepository.findByname(wholesaleSignupDTO.getName()).isEmpty()){
            logger.info("도매 회원가입 진행");
            User user = User.builder()
                            .role(wholesaleSignupDTO.getRole())
                            .email(wholesaleSignupDTO.getEmail())
                            // .password(bCryptPasswordEncoder.encode(wholesaleSignupDTO.getPassword()))
                            .password(wholesaleSignupDTO.getPassword())
                            .contact(wholesaleSignupDTO.getContact())
                            .businessNumber(wholesaleSignupDTO.getBusinessNumber())
                            .name(wholesaleSignupDTO.getName())
                            .alertType(wholesaleSignupDTO.isAlertType())
                            .termsType(wholesaleSignupDTO.isTermsType())
                            .socialType(wholesaleSignupDTO.isSocialType())
                            .socialName(wholesaleSignupDTO.getSocialName())
                            .build(); 
            logger.info("유저 저장");
            userRepository.save(user);

            Shop shop = Shop.builder()
                            .id(user.getId())
                            .contact(wholesaleSignupDTO.getShopContact())
                            .name(wholesaleSignupDTO.getShopName())
                            .intro(wholesaleSignupDTO.getIntro())
                            .profileType(wholesaleSignupDTO.isProfileType())
                            .roadAddress(wholesaleSignupDTO.getShopRoadAddress())
                            .addressDetails(wholesaleSignupDTO.getShopAddressDetail())
                            .startTime(wholesaleSignupDTO.getStartTime())
                            .endTime(wholesaleSignupDTO.getEndTime())
                            .registrationDateTime(wholesaleSignupDTO.getRegistrationDateTime())
                            .build();
            logger.info("가게 저장");
            shopRepository.save(shop);
        }else{
            logger.info("도매 회원가입 데이터 존재.");
            throw new IllegalArgumentException("이미 사용 중인 이메일 or 가게입니다.");
        }
    }

    // public void login(UserDTO userDTO){
    //     try{
    //         logger.info("객체 생성");
    //         UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword());
    //         logger.info("검증 진행");
    //         Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    //     } catch (DisabledException e){
    //         logger.info("DisabledException");
    //     } catch (LockedException e){
    //         logger.info("LockedException");
    //     } catch (BadCredentialsException e){
    //         logger.info("BadCredentialsException");
    //     }
    //     logger.info("검증 완료");
    // }

}
