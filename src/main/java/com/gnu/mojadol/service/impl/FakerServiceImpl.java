package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.FakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FakerServiceImpl implements FakerService {

    @Autowired
    private UserRepository userRepository;

    public void userFakeData() {

    }
}
