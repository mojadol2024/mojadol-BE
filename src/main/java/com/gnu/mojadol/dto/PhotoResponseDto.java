package com.gnu.mojadol.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponseDto {
    private int photoSeq;      // 사진 고유 번호
    // private int userSeq; // 사용자 ID (USER 테이블의 외래 키)
    private int boardSeq;      // 게시글 고유 번호
    private String filePath;   // 사진 파일 경로
    private String uploadDate; // 업로드 날짜 (클라이언트에게 제공)
}
