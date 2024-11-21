package com.gnu.mojadol.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Data
public class MailDto {
    private String title;
    private String address;
    private String message;
}
