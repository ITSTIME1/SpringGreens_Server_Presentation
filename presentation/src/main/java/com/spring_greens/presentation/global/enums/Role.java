package com.spring_greens.presentation.global.enums;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_SOCIAL("ROLE_SOCIAL", "일반 사용자"),
    ROLE_RETAILER("ROLE_RETAILER", "소매업자 사용자"),
    ROLE_WHOLESALER("ROLE_WHOLESALER", "도매업자 사용자");

    private final String roleName;
    private final String discription;

    Role(String roleName, String discription) {
        this.roleName = roleName;
        this.discription = discription; }
}
