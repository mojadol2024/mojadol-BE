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
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto) {
        User user = null;
        Board board = null;
        if (commentRequestDto != null) {
            user = userRepository.findByUserSeq(commentRequestDto.getUserSeq());
            board = boardRepository.findByBoardSeq(commentRequestDto.getBoardSeq());

            if (board == null) {
                throw new IllegalArgumentException("게시글을 찾을 수 없습니다."); // 예외 처리
            }

            if (user == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다."); // 예외 처리
            }
        }
        Date date = new Date(); // db를 보시면 String으로 받게 되어있어서 Date는 type이 맞지 않아요 String으로 변환 해줄게요
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);

        Comment comment = new Comment();
        comment.setCommentText(commentRequestDto.getCommentText());
        comment.setBoard(board);
        comment.setUser(user);
        comment.setCreatedAt(dateString); // 현재 시간 설정

        Comment saved = commentRepository.save(comment);

        return getCommentResponseDto(saved);
    }


    private static CommentResponseDto getCommentResponseDto(Comment saved) {
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setCommentSeq(saved.getCommentSeq());
        commentResponseDto.setCommentText(saved.getCommentText());
        commentResponseDto.setBoardSeq(saved.getBoard().getBoardSeq());
        commentResponseDto.setCreatedAt(saved.getCreatedAt());
        if (saved.getParentCommentSeq() != null) {
            commentResponseDto.setParentCommentSeq(saved.getParentCommentSeq());
        }else {
            commentResponseDto.setParentCommentSeq(null);  // 부모 댓글이 없으면 null로 처리
        }
        commentResponseDto.setDeletedFlag(saved.getDeletedFlag());
        commentResponseDto.setNickName(saved.getUser().getNickname());
        return commentResponseDto;
    }

    // 2. 특정 게시글에 대한 모든 댓글 조회
    public List<CommentResponseDto> getCommentsByBoardSeq(int boardSeq) {
        List<Comment> comments = commentRepository.findByBoard_BoardSeq(boardSeq)
                .stream()
                .toList();

        return comments.stream().map(comment -> {
            CommentResponseDto dto = new CommentResponseDto();
            dto.setCommentSeq(comment.getCommentSeq());
            dto.setCommentText(comment.getCommentText());
            dto.setBoardSeq(comment.getBoard().getBoardSeq());
            dto.setUserSeq(comment.getUser().getUserSeq());
            dto.setNickName(comment.getUser().getNickname());
            dto.setDeletedFlag(comment.getDeletedFlag());
            if (comment.getParentCommentSeq() != null) {
                dto.setParentCommentSeq(comment.getParentCommentSeq());
            } else {
                dto.setParentCommentSeq(null);  // 부모 댓글이 없으면 null로 처리
            }
            dto.setCreatedAt(comment.getCreatedAt());
            dto.setUpdatedAt(comment.getUpdatedAt());
            return dto;
        }).collect(Collectors.toList());
    }


    // 3. 특정 댓글 수정
    public CommentResponseDto updateComment(CommentRequestDto commentRequestDto) {
        if (commentRequestDto != null) {
            Comment comment = commentRepository.findById(commentRequestDto.getCommentSeq())
                    .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

            comment.setCommentText(commentRequestDto.getCommentText());
            comment.setUpdatedAt(LocalDateTime.now()); // 수정 시 현재 시간 설정
            Comment updatedComment = commentRepository.save(comment);

            return getCommentResponseDto(updatedComment);
        }
        throw new IllegalArgumentException("유효하지 않는 요청입니다.");
    }

    // 4. 특정 댓글 삭제
    public void deleteComment(CommentRequestDto commentRequestDto) {
        if (!commentRepository.existsById(commentRequestDto.getCommentSeq()) && !userRepository.existsById(commentRequestDto.getUserSeq())) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다. 혹은 유저를 찾을 수 없습니다.");
        }
        Comment comment = commentRepository.findByCommentSeq(commentRequestDto.getCommentSeq());
        comment.setDeletedFlag(1);

        commentRepository.save(comment);
    }

    //답글작성
    public CommentResponseDto reply(CommentRequestDto commentRequestDto) {
        User user = null;
        Board board = null;
        if (commentRequestDto != null) {
            user = userRepository.findByUserSeq(commentRequestDto.getUserSeq());
            board = boardRepository.findByBoardSeq(commentRequestDto.getBoardSeq());

            if (board == null) {
                throw new IllegalArgumentException("게시글을 찾을 수 없습니다."); // 예외 처리
            }

            if (user == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다."); // 예외 처리
            }
        }
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);

        Comment comment = new Comment();
        comment.setCommentText(commentRequestDto.getCommentText());
        comment.setBoard(board);
        comment.setUser(user);
        comment.setParentCommentSeq(commentRequestDto.getParentCommentSeq());
        comment.setCreatedAt(dateString); // 현재 시간 설정

        Comment saved = commentRepository.save(comment);

        return getCommentResponseDto(saved);

    }

    /*
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
    */
}

