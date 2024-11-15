package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.MyPageActivityDto;
import com.gnu.mojadol.entity.MyPageActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MyPageActivityService {
    Page<MyPageActivityDto> getUserActivities(int userSeq, Pageable pageable); // 게시글과 댓글을 한 번에 조회
    Page<MyPageActivityDto> getUserPosts(int userSeq, Pageable pageable); // 게시글만 조회
    Page<MyPageActivityDto> getUserComments(int userSeq, Pageable pageable); // 댓글만 조회
    void deleteUserActivity(Long activityId); // 특정 활동 삭제 (논리 삭제)
    MyPageActivityDto getActivityDetail(Long activityId); // 특정 활동 상세 조회(사용자가 나의 활동 페이지에서 특정 항목(게시글이나 댓글)을 클릭했을 때, 그 항목의 상세 페이지로 넘어가기 위한 기능)
}

