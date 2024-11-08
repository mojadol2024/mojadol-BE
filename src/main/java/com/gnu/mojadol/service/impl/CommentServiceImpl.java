package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.CommentRequestDto;
import com.gnu.mojadol.dto.CommentResponseDto;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.Comment;
import com.gnu.mojadol.repository.BoardRepository;
import com.gnu.mojadol.repository.CommentRepository;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository; // 소문자로 수정

    // 1. 댓글 작성
    @Override
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto) {
        // 로그 추가: addComment 메소드의 초기 입력값을 확인
        System.out.println("Adding comment to boardSeq: " + commentRequestDto.getBoardSeq());
        System.out.println("UserSeq: " + commentRequestDto.getUserSeq());
        System.out.println("CommentText: " + commentRequestDto.getCommentText());

        // 게시글 존재 여부 사전 확인
        int boardSeq = commentRequestDto.getBoardSeq();
        if (!boardRepository.existsById(boardSeq)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }

        // 게시글을 찾기
        Board board = boardRepository.findById(boardSeq)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById((long) commentRequestDto.getUserSeq())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setCommentText(commentRequestDto.getCommentText());
        comment.setBoard(board);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now()); // 현재 시간 설정
        comment.setUpdatedAt(LocalDateTime.now()); // 현재 시간 설정

        Comment savedComment = commentRepository.save(comment);
        return getCommentResponseDto(savedComment, board, user);
    }


    private static CommentResponseDto getCommentResponseDto(Comment savedComment, Board board, User user) {
        CommentResponseDto responseDto = new CommentResponseDto();
        responseDto.setCommentSeq(savedComment.getCommentSeq());
        responseDto.setCommentText(savedComment.getCommentText());
        responseDto.setBoardSeq(savedComment.getBoard().getBoardSeq());
        // responseDto.setNickName(user.getNickname()); // 필요 시 작성자의 닉네임 설정
        responseDto.setUserSeq(savedComment.getUser().getUserSeq()); // 작성자 ID 추가
        responseDto.setAuthor(board.getUser().getUserSeq() == user.getUserSeq());
        return responseDto;
    }

    // 2. 특정 게시글에 대한 모든 댓글 조회
    public List<CommentResponseDto> getCommentsByBoardSeq(int boardSeq) {
        List<Comment> comments = commentRepository.findByBoard_BoardSeq(boardSeq)
                .stream()
                .filter(comment -> comment.getDeletedFlag() == 0) // 삭제되지 않은 댓글만
                .toList();
        return comments.stream().map(comment -> {
            CommentResponseDto dto = new CommentResponseDto();
            dto.setCommentSeq(comment.getCommentSeq());
            dto.setCommentText(comment.getCommentText());
            dto.setBoardSeq(comment.getBoard().getBoardSeq());
            dto.setUserSeq(comment.getUser().getUserSeq()); // 작성자 ID 추가
            return dto;
        }).collect(Collectors.toList());
    }

    // 3. 특정 댓글 수정
    @Override
    public CommentResponseDto updateComment(int commentSeq, CommentRequestDto commentRequestDto) {
        if (commentRequestDto != null) {
            Comment comment = commentRepository.findById(commentSeq)
                    .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

            comment.setCommentText(commentRequestDto.getCommentText());
            comment.setUpdatedAt(LocalDateTime.now()); // 수정 시 현재 시간 설정
            Comment updatedComment = commentRepository.save(comment);

            return getCommentResponseDto(updatedComment, updatedComment.getBoard(), updatedComment.getUser());
        }
        throw new IllegalArgumentException("유효하지 않는 요청입니다.");
    }

    // 4. 특정 댓글 삭제
    @Override
    public void deleteComment(int commentSeq) {
        if (!commentRepository.existsById(commentSeq)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        commentRepository.deleteById(commentSeq);
    }

    // 5. 특정 댓글 조회
    @Override
    public CommentResponseDto getCommentBySeq(int commentSeq) {
        Comment comment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        CommentResponseDto responseDto = new CommentResponseDto();
        responseDto.setCommentSeq(comment.getCommentSeq());
        responseDto.setCommentText(comment.getCommentText());
        responseDto.setBoardSeq(comment.getBoard().getBoardSeq());

        return responseDto;
    }
}
