package com.gnu.mojadol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponseDto {
    // 클라이언트에 반환할 때 필요한 모든 사진 정보(파일 경로와 업로드 날짜 등)를 포함
    private int photoSeq;        // 사진 고유 번호
    private int boardSeq;        // 게시글 고유 번호
    private String filePath;     // 사진 파일 경로
    private String uploadDate;   // 업로드 날짜

}
