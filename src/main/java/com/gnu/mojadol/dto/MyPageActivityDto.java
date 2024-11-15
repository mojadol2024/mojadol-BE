package com.gnu.mojadol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPageActivityDto { // 마이페이지 나의 활동 dto
    private int boardSeq;          // 게시판 글 번호
    private int userSeq;         // 작성자 식별자
    private String dogName;        // 개 이름
    private String breedName;      // 견종 이름
    private String lostDate;       // 실종 날짜
    private String createdAt;   // 게시글 작성일 또는 댓글 작성일
    private LocalDateTime updatedAt;   // 게시글 수정일 또는 댓글 수정일 (수정된 경우에만 사용)
    private String activityType;   // 활동 종류 ("POST" 또는 "COMMENT")
    private int parentBoardSeq;    // 댓글일 경우 연관된 게시글 번호(댓글인지 아닌지를 찾는 게 아니라, 댓글이 어느 게시물에 있는지를 확신시키는 것)
    private int deletedFlag;   // 삭제 유무
}
