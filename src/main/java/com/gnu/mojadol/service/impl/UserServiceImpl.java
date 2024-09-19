package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.UserRequestDto;
import com.gnu.mojadol.dto.UserResponseDto;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserResponseDto addUser(UserRequestDto dto) {
        System.out.println("UserServiceImpl addUser: " + dto);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 날짜를 문자열로 변환
        String dateString = dateFormat.format(date);

        User user = new User();
        user.setUserId(dto.getUserId());
        user.setName(dto.getName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAlertBoolean(1);
        user.setAlertCount(0);
        user.setRegiTime(dateString);
        User savedUser = userRepository.save(user);
        // ResponseDto 생성 후 반환
        UserResponseDto response = new UserResponseDto();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setAlertCount(0);
        response.setAlertBoolean(1);
        response.setMessage("YES");
        response.setSuccess(true);

        return response;
    }
}

