package com.gnu.mojadol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto { // 게시글 댓글 DTO
    private int commentSeq;           // 댓글 ID
    private int boardSeq;             // 댓글이 연결된 게시글 ID
    private int userSeq;              // 댓글 작성자 ID
    private boolean deletedFlag;       // 삭제 유무 (false = 삭제되지 않음, true = 삭제됨)
    private int parentCommentSeq;     // 부모 댓글 ID (답글을 위한 자기 참조)
    private String commentText;        // 댓글 내용
    private LocalDateTime createdAt;   // 생성 날짜
    private LocalDateTime updatedAt;   // 수정 날짜
    private boolean Author;          // 게시글 작성자 여부 추가
}

