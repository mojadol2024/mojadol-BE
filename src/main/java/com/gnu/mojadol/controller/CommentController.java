package com.gnu.mojadol.controller;

import com.gnu.mojadol.dto.CommentRequestDto;
import com.gnu.mojadol.dto.CommentResponseDto;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.CommentService;
import com.gnu.mojadol.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    // 댓글 추가
    @PostMapping("/write/board/{boardSeq}")
    public ResponseEntity<String> addComment(@RequestBody CommentRequestDto commentRequestDto,
                                             @RequestHeader("Authorization") String accessToken) {
        System.out.println("CommentController addComment " + new Date());

        String userId = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUserId(userId);
        commentRequestDto.setUserSeq(user.getUserSeq());
        commentService.addComment(commentRequestDto);
        return ResponseEntity.ok("Comment added successfully");
    }

    // 특정 게시글의 댓글 조회
    @GetMapping("/board/{boardSeq}")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable int boardSeq) {
        System.out.println("CommentController getCommentsByBoardSeq " + new Date());

        List<CommentResponseDto> comments = commentService.getCommentsByBoardSeq(boardSeq);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정
    @PutMapping("/edit/{commentSeq}")
    public ResponseEntity<CommentResponseDto> update(@PathVariable int commentSeq,
                                                     @RequestBody CommentRequestDto commentRequestDto,
                                                     @RequestHeader("Authorization") String accessToken) {
        System.out.println("CommentController updateComment " + new Date());

        String userId = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUserId(userId);
        commentRequestDto.setUserSeq(user.getUserSeq());
        CommentResponseDto updatedCommentDto = commentService.updateComment(commentSeq, commentRequestDto);
        return ResponseEntity.ok(updatedCommentDto);
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{commentSeq}")
    public ResponseEntity<String> delete(@PathVariable int commentSeq) {
        System.out.println("CommentController deleteComment " + new Date());

        commentService.deleteComment(commentSeq);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    // 특정 댓글 조회
    @GetMapping("/{commentSeq}")
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable int commentSeq) {
        System.out.println("CommentController getCommentBySeq " + new Date());

        CommentResponseDto responseDto = commentService.getCommentBySeq(commentSeq);
        return ResponseEntity.ok(responseDto);
    }
}
