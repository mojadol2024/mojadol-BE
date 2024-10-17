package com.gnu.mojadol.entity;

import lombok.Data;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@Entity
@Table(name = "USER")
public class User implements UserDetails {
// @GeneratedValue(strategy = GenerationType.IDENTITY) auto_increment일때 사용

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_seq")
    private int userSeq;

    @Column(nullable = false, unique = true)
    private String userId;
    private String phoneNumber;
    @Column(nullable = false)
    private String userPw;
    private String userName;
    private String nickname;
    private String regiDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<GrantedAuthority> authorities;

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return userPw;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성 여부
    }



}