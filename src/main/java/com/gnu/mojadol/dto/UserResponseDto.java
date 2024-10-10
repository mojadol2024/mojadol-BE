package com.gnu.mojadol.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private String userId;
    private String userName;
    private boolean success;
    private String message;

}
