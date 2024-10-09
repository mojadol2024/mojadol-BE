package com.gnu.mojadol.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String userId;
    private String userPw;
}
