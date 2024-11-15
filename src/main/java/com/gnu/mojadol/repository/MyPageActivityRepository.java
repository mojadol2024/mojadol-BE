package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.MyPageActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyPageActivityRepository extends JpaRepository<MyPageActivity, Long> {

    // 모든 활동을 페이징으로 조회
    Page<MyPageActivity> findAll(Pageable pageable);

    // 특정 사용자가 작성한 모든 활동 (게시글과 댓글 모두) 페이징 조회
    Page<MyPageActivity> findByUser_UserSeq(int userSeq, Pageable pageable);

    // 특정 사용자가 작성한 게시글만 페이징 조회
    Page<MyPageActivity> findByUser_UserSeqAndActivityType(int userSeq, String activityType, Pageable pageable);

    // 특정 사용자가 작성한 댓글만 페이징 조회
    default Page<MyPageActivity> findPostsByUser(int userSeq, Pageable pageable) {
        return findByUser_UserSeqAndActivityType(userSeq, "POST", pageable);
    }

    default Page<MyPageActivity> findCommentsByUser(int userSeq, Pageable pageable) {
        return findByUser_UserSeqAndActivityType(userSeq, "COMMENT", pageable);
    }
}
