// 인터페이스
package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.UserRequestDto;
import com.gnu.mojadol.dto.UserResponseDto;

public interface UserService {
    UserResponseDto addUser(UserRequestDto dto);
}
