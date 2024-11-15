package com.gnu.mojadol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreedDto { // 견종 분류 기능 시 사용할 dto
    private String breedName;
}