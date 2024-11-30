package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.UserRequestDto;
import com.gnu.mojadol.dto.UserResponseDto;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    public PasswordEncoder passwordEncoder;

    public User getUserByUserName(String userName) {
        return userRepository.findByUserId(userName);
    }
    @Override
    public UserResponseDto addUser(UserRequestDto dto) {
        System.out.println("UserServiceImpl addUser: " + dto);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedPhoneNumber = formatPhoneNumber(dto.getPhoneNumber());

        // 날짜를 문자열로 변환
        String dateString = dateFormat.format(date);

        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUserName(dto.getUserName());
        user.setPhoneNumber(formattedPhoneNumber);
        user.setRegiDate(dateString);
        user.setNickname(dto.getNickName());
        user.setUserPw(passwordEncoder.encode(dto.getUserPw()));
        user.setMail(dto.getMail());
        userRepository.save(user);
        // ResponseDto 생성 후 반환
        UserResponseDto response = new UserResponseDto();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUsername());
        response.setMessage("YES");
        response.setSuccess(true);

        return response;
    }

    @Override
    public void updatePassword(UserRequestDto userRequestDto) {

        User user = userRepository.findByUserIdAndMail(userRequestDto.getUserId(), userRequestDto.getMail());

        user.setUserPw(passwordEncoder.encode(userRequestDto.getUserPw()));

        userRepository.save(user);
    }

    @Override
    public String checkId(String userId) {
        System.out.println(userId);
        User user = userRepository.findByUserId(userId);
        System.out.println(user);

        return (user != null) ? user.getUserId() : "";
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            throw new IllegalArgumentException("formatPhoneNumber error");
        }
        return phoneNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }


}

