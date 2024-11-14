package com.gnu.mojadol.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LOCATION")
@Data
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_seq")
    private int locationSeq;

    @Column(nullable = false, name = "province")
    private String province;

    @Column(nullable = false, name = "city")
    private String city;

    @Column(nullable = false, name = "district")
    private String district;
}
