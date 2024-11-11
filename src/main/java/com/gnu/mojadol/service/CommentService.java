package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.CommentRequestDto;
import com.gnu.mojadol.dto.CommentResponseDto;

public interface CommentService {

    // 1. 댓글 작성
    CommentResponseDto addComment(CommentRequestDto commentRequestDto);

    // 2. 특정 게시글에 대한 모든 댓글 조회
    //List<CommentResponseDto> getCommentsByBoardSeq(int boardSeq);

    // 3. 특정 댓글 수정
    CommentResponseDto updateComment(CommentRequestDto commentRequestDto);

    // 4. 특정 댓글 삭제
    void deleteComment(CommentRequestDto commentRequestDto);

    // 5. 특정 댓글 조회 - 예)댓글 ID가 456인 댓글 하나의 정보를 조회
    //CommentResponseDto getCommentBySeq(int commentSeq);

}
