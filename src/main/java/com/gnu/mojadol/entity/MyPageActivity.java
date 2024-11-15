package com.gnu.mojadol.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "MYPAGEACTIVITY")
@Data
@NoArgsConstructor
public class MyPageActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId; // 마이페이지 활동의 고유 식별자 (기본 키)

    @ManyToOne
    @JoinColumn(name = "board_seq", nullable = false)
    private Board board; // 게시판 글 정보 (외래 키)

    @ManyToOne
    @JoinColumn(name = "user_seq", referencedColumnName = "user_seq", nullable = false)
    private User user;  // 작성자 식별자

    @Column(nullable = false, name = "dog_name")
    private String dogName;  // 개 이름

    @Column(name = "breed_name", nullable = false)
    private String breedName; // 견종 이름

    @Column(nullable = false, name = "lost_date")
    private String lostDate;    // 실종 날짜

    @Column(nullable = false, name = "created_at")
    private String createdAt;    // 게시글 작성일

    @Column(nullable = false, name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;    // 게시글 수정일

    @Column(name = "activity_type")
    private String activityType;   // 활동 종류 ("POST" 또는 "COMMENT")

    @ManyToOne
    @JoinColumn(name = "parent_board_seq")
    private Board parentBoardSeq;  // 댓글일 경우 연관된 게시글 번호

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private int deletedFlag;  // 삭제 여부
}
