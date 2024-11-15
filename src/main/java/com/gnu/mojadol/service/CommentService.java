package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.CommentRequestDto;
import com.gnu.mojadol.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {

    CommentResponseDto addComment(CommentRequestDto commentRequestDto);

    List<CommentResponseDto> getCommentsByBoardSeq(int boardSeq);

    CommentResponseDto updateComment(CommentRequestDto commentRequestDto);

    void deleteComment(CommentRequestDto commentRequestDto);

    // 5. 특정 댓글 조회 - 예)댓글 ID가 456인 댓글 하나의 정보를 조회
    //CommentResponseDto getCommentBySeq(int commentSeq);

}
