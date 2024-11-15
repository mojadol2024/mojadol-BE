package com.gnu.mojadol.controller;

import com.gnu.mojadol.dto.CommentResponseDto;
import com.gnu.mojadol.dto.MyPageActivityDto;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.MyPageActivityService;
import com.gnu.mojadol.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/myActivity")
public class MyPageActivityController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyPageActivityService myPageActivityService;

    @Autowired
    private UserRepository userRepository;

    // 사용자 인증 메서드
    private User authenticateUser(String accessToken, int userSeq) {
        String userId = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUserId(userId);
        if (user == null || user.getUserSeq() != userSeq) {
            return null;
        }
        return user;
    }

    // 사용자 전체 활동 조회
    @PostMapping("/allList")
    public ResponseEntity<Page<MyPageActivityDto>> getUserActivities(@RequestBody MyPageActivityDto requestDto, Pageable pageable, @RequestHeader("Authorization") String accessToken) {
        System.out.println("MyPageActivityController checkList " + new Date());

        // 사용자 인증
        User user = authenticateUser(accessToken, requestDto.getUserSeq());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 서비스에서 사용자 활동을 조회합니다
        Page<MyPageActivityDto> activities = myPageActivityService.getUserActivities(requestDto.getUserSeq(), pageable);
        return ResponseEntity.ok(activities);
    }

    // 사용자 게시글 조회
    @PostMapping("/posts")
    public ResponseEntity<Page<MyPageActivityDto>> getUserPosts(@RequestBody MyPageActivityDto requestDto, Pageable pageable, @RequestHeader("Authorization") String accessToken) {
        // 사용자 인증
        User user = authenticateUser(accessToken, requestDto.getUserSeq());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 서비스에서 사용자 게시글을 조회합니다
        Page<MyPageActivityDto> posts = myPageActivityService.getUserPosts(requestDto.getUserSeq(), pageable);
        return ResponseEntity.ok(posts);
    }

    // 사용자 댓글 조회
    @PostMapping("/comments")
    public ResponseEntity<Page<MyPageActivityDto>> getUserComments(@RequestBody MyPageActivityDto requestDto, Pageable pageable, @RequestHeader("Authorization") String accessToken) {
        // 사용자 인증
        User user = authenticateUser(accessToken, requestDto.getUserSeq());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 서비스에서 사용자 댓글을 조회합니다
        Page<MyPageActivityDto> comments = myPageActivityService.getUserComments(requestDto.getUserSeq(), pageable);
        return ResponseEntity.ok(comments);
    }

    // 특정 활동 삭제 (논리 삭제)
    @PostMapping("/delete/{activityId}")
    public ResponseEntity<String> deleteUserActivity(@PathVariable Long activityId, @RequestBody MyPageActivityDto requestDto, @RequestHeader("Authorization") String accessToken) {
        System.out.println("MyPageActivityController deleteUserActivity " + new Date());

        // 사용자 인증
        User user = authenticateUser(accessToken, requestDto.getUserSeq());
        MyPageActivityDto activity = myPageActivityService.getActivityDetail(activityId);
        if (user == null || activity == null || activity.getUserSeq() != user.getUserSeq()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        // 서비스에서 활동을 조회합니다
        MyPageActivityDto retrievedActivity; // 변수 이름 변경
        try {
            retrievedActivity = myPageActivityService.getActivityDetail(activityId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 활동을 찾을 수 없습니다.");
        }

        // 활동 소유자 확인
        if (retrievedActivity.getUserSeq() != user.getUserSeq()) { // 변수 이름 사용 시 혼동 없도록 수정
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        // 서비스에서 활동을 논리적으로 삭제합니다
        myPageActivityService.deleteUserActivity(activityId);
        return ResponseEntity.ok("YES");
    }

    // 특정 활동 상세 조회
    @PostMapping("/detail/{activityId}")
    public ResponseEntity<MyPageActivityDto> getActivityDetail(@PathVariable Long activityId, @RequestBody MyPageActivityDto requestDto, @RequestHeader("Authorization") String accessToken) {
        System.out.println("MyPageActivityController getActivityDetail " + new Date());

        // 사용자 인증
        User user = authenticateUser(accessToken, requestDto.getUserSeq());
        MyPageActivityDto activityDetail = myPageActivityService.getActivityDetail(activityId);
        if (user == null || activityDetail == null || activityDetail.getUserSeq() != user.getUserSeq()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(activityDetail);
    }
}
