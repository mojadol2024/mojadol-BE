package com.gnu.mojadol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto { // 게시글 댓글 dto
    private int boardSeq;             // 댓글이 연결된 게시글 ID
    private int userSeq;              // 댓글 작성자 ID
    private String commentText;        // 댓글 내용
    private int parentCommentSeq;     // 부모 댓글 ID (답글을 위한 자기 참조)
    private int commentSeq;
    private int deleted_flag;   // dto에는 유저한테 받는값이랑 백엔드에서 수정할 값들을 넣어야해요 필요하시면 더 추가하셔도 됩니다
}
