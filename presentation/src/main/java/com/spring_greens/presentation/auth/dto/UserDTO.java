package com.spring_greens.presentation.auth.dto;

import com.spring_greens.presentation.global.enums.Role;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class UserDTO {
    private long id;
    private Role role;
    private String name;
    private String email;
    private String password;
}
