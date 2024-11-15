package com.gnu.mojadol.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PhotoRequestDto {
    private int boardSeq;      // 게시글 고유 번호
    private String filePath;   // 사진 파일 경로

}
