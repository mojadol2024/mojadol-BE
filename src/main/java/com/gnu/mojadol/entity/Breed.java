package com.gnu.mojadol.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BREED")
@Data
@NoArgsConstructor
public class Breed {

    @Id
    @Column(name = "breed_name")
    private String breedName;
}
