// 인터페이스
package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.UserRequestDto;
import com.gnu.mojadol.dto.UserResponseDto;
import com.gnu.mojadol.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface UserService {
    UserResponseDto addUser(UserRequestDto dto);

    void updatePassword(UserRequestDto dto);
}
