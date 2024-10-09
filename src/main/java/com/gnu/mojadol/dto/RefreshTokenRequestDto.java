package com.gnu.mojadol.dto;

import lombok.Data;

@Data
public class RefreshTokenRequestDto {
    private String refreshToken;
    private String userName;
}
