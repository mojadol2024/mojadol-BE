package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername CustomUserDetailServiceImpl" + new Date());
        User user = userRepository.findByUserId(userId);

        // 사용자 존재 여부 검사s
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(), user.getAuthorities());
    }
}
