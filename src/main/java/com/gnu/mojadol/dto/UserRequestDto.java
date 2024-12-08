package com.gnu.mojadol.dto;
import lombok.Data;
@Data
public class UserRequestDto {
    private int userSeq;
    private String userId;
    private String userPw;
    private String phoneNumber;
    private String nickName;
    private String userName;
    private String regiTime;
    private String mail;
    private String code;
}
