package com.gnu.mojadol.dto;

import lombok.Data;

import java.util.List;

@Data
public class NotificationRequestDto {
    private String userId;
    private String title;
    private String body;
    private String token;
}
