package com.gnu.mojadol.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BOARD")
@Data
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_seq")
    private int boardSeq;

    @ManyToOne
    @JoinColumn(name = "breed_name", referencedColumnName = "breed_name", nullable = false)
    private Breed breed;

    @ManyToOne
    @JoinColumn(name = "user_seq", referencedColumnName = "user_seq", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_seq", referencedColumnName = "location_seq", nullable = false)
    private Location location;

    @Column(nullable = false)
    private int report;

    @Column(nullable = false, name = "dog_name")
    private String dogName;

    @Column(nullable = false, name = "dog_age")
    private String dogAge;

    @Column(nullable = false, name = "dog_gender")
    private int dogGender;

    @Column(nullable = false, name = "dog_weight")
    private String dogWeight;

    @Column(nullable = false, name = "lost_date")
    private String lostDate;

    @Column(nullable = false, name = "post_date")
    private String postDate;

    @Column(length = 3000)
    private String memo;
}
