package com.spring_greens.presentation.global.enums;

import lombok.Getter;

@Getter
public enum Mall {
    APM( 2, "apm"),
    DONGPYEONGHWA(3,"동평화시장"),
    CHEONGPYEONGHWA(4, "청평화시장"),
    JEIL(5, "제일평화시장");

    private final int mallId;
    private final String mallName;
    Mall(int mallId, String mallName) {
        this.mallId = mallId;
        this.mallName = mallName;
    }
}
