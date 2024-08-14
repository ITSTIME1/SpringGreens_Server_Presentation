package com.spring_greens.presentation.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenDTO {
    private String grantType;
    private String accessToken;
    private String refreshToken;

   @Builder
   public TokenDTO(String grantType, String accessToken, String refreshToken){
    this.grantType = grantType;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;

   }
}
