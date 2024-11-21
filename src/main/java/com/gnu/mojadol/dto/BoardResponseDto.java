package com.gnu.mojadol.dto;

import com.gnu.mojadol.entity.Photo;
import lombok.Data;

import java.util.List;

@Data
public class BoardResponseDto {
    private int boardSeq;       // 게시글 ID
    //private String title;       // 게시글 제목
    private String breedName;   // 견종 이름
    private int report;         // 제보(1) / 실종 신고(0)
    private String dogName;     // 개 이름
    private String dogAge;      // 개 나이
    private int dogGender;      // 0 여자 / 1 남자
    private String dogWeight;   // 개 몸무게
    private String lostDate;    // 실종일
    private String postDate;    // 게시일
    private String memo;        // 메모
    private int userSeq;        // 사용자 ID (USER 테이블의 외래 키)
    private String nickName;
    private String location;
    private List<String> photos;
}
