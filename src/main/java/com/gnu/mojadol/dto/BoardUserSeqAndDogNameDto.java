package com.gnu.mojadol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardUserSeqAndDogNameDto {
    private int userSeq;
    private String dogName;
}
