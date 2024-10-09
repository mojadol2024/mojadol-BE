package com.gnu.mojadol.controller;

import com.gnu.mojadol.dto.UserRequestDto;
import com.gnu.mojadol.dto.UserResponseDto;
import com.gnu.mojadol.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

}
