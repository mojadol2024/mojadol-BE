package com.gnu.mojadol.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "User")
public class User {
// @GeneratedValue(strategy = GenerationType.IDENTITY) auto_increment일때 사용
    @Id
    @Column(nullable = false, unique = true)
    private String userId;

    private String phoneNumber;
    private String name;
    private int alertCount;
    private int alertBoolean;
    private String regiTime;
}