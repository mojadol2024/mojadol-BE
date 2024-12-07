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

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    // 댓글 추가
    @PostMapping("/write")
    public ResponseEntity<?> addComment(@RequestBody CommentRequestDto commentRequestDto,
                                             @RequestHeader("Authorization") String accessToken) {
        System.out.println("CommentController addComment " + new Date());
        System.out.println(commentRequestDto);

        String userId = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUserId(userId);
        commentRequestDto.setUserSeq(user.getUserSeq());
        CommentResponseDto commentResponseDto = commentService.addComment(commentRequestDto);
        return ResponseEntity.ok(commentResponseDto);
    }

    // 댓글 수정
    @PostMapping("/edit")
    public ResponseEntity<CommentResponseDto> update(@RequestBody CommentRequestDto commentRequestDto,
                                                     @RequestHeader("Authorization") String accessToken) {
        System.out.println("CommentController updateComment " + new Date());
        System.out.println(commentRequestDto);
        String userId = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUserId(userId);
        commentRequestDto.setUserSeq(user.getUserSeq());
        CommentResponseDto updatedCommentDto = commentService.updateComment(commentRequestDto);
        return ResponseEntity.ok(updatedCommentDto);
    }

    // 댓글 삭제
    @PostMapping("/delete") // Get Post 두가지만 쓰면 돼요!! get은 client에서 받을 값이 없을때 post는 값을 받아올때
    public ResponseEntity<String> delete(@RequestBody CommentRequestDto commentRequestDto, @RequestHeader("Authorization") String accessToken) {
        System.out.println("CommentController deleteComment " + new Date());
        System.out.println(commentRequestDto);
        //댓글을 삭제할려면 본인 댓글만 삭제해야하니까 token에서 userSeq를 추출해서 대조해봐야해요
        // 예시로 코드 작성 해놓을게요
        String userId = jwtUtil.extractUsername(accessToken); // jwt에서 user의 id값을 넣어놓은걸 추출합니다
        User user = userRepository.findByUserId(userId); // 그 id값을 userRepository에 만들어 놓은 user를 찾아요
        commentRequestDto.setUserSeq(user.getUserSeq()); // commentRequestDto값에 userSeq값을 넣어줍니다.
        commentService.deleteComment(commentRequestDto); // dto를 통째로 보내요 service에 그러면 service가 impl에서 처리합니다
        return ResponseEntity.ok("YES"); // 프론트가 편하게 YES 또는 OK로 통일 하는게 좋을거 같아요  Httpstate에 OK가 따로 있긴합니다
    }

    @PostMapping("/reply")
    public ResponseEntity<?> reply(@RequestBody CommentRequestDto commentRequestDto,
                                        @RequestHeader("Authorization") String accessToken) {
        System.out.println("CommentController reply" + new Date());
        System.out.println(commentRequestDto);
        String userId = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUserId(userId);
        commentRequestDto.setUserSeq(user.getUserSeq());
        CommentResponseDto commentResponseDto = commentService.reply(commentRequestDto);
        return ResponseEntity.ok(commentResponseDto);
    }
    /*
    // 특정 댓글 조회
    @GetMapping("/{commentSeq}")
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable int commentSeq) {
        System.out.println("CommentController getCommentBySeq " + new Date());

        CommentResponseDto responseDto = commentService.getCommentBySeq(commentSeq);
        return ResponseEntity.ok(responseDto);
    }
    */
}
