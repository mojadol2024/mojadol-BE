package com.gnu.mojadol.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private String userId;
    private String name;
    private int alertCount;
    private int alertBoolean;
    private boolean success;
    private String message;
}