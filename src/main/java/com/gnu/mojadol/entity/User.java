package com.gnu.mojadol.entity;

import lombok.Data;
import jakarta.persistence.*;
import org.checkerframework.checker.units.qual.C;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@Entity
@Table(name = "USER")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_seq")
    private int userSeq;

    @Column(nullable = false, unique = true, name = "user_id")
    private String userId;

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false, name = "user_pw")
    private String userPw;

    @Column(nullable = false, name = "user_name")
    private String userName;

    @Column(nullable = false, name = "nickname")
    private String nickname;

    @Column(nullable = false, name = "regi_date")
    private String regiDate;

    @Column(nullable = false, name = "mail")
    private String mail;

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