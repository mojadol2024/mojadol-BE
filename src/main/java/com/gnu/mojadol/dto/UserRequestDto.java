package com.gnu.mojadol.dto;
import lombok.Data;
@Data
public class UserRequestDto {
    private String userId;
    private String phoneNumber;
    private String name;
    private int alertCount; // 0:강아지 못찾은 유저 | 1:잃어버린 강아지를 찾은 유저 2: 3 4
    private int alertBoolean; // 0: 알림끄기 / 1:알림키기
    private String regiTime;
}
