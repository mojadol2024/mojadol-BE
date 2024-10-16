package com.gnu.mojadol.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "COMMENT")
@Data
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_seq")
    private int commentSeq;  // 기본 키

    @ManyToOne
    @JoinColumn(name = "board_seq", referencedColumnName = "board_seq", nullable = false)
    private Board board;  // BOARD 외래 키

    @ManyToOne
    @JoinColumn(name = "user_seq", referencedColumnName = "user_seq", nullable = false)
    private User user;  // USER 외래 키

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private int deletedFlag;  // 삭제 여부

    @ManyToOne
    @JoinColumn(name = "parent_comment_seq", referencedColumnName = "comment_seq")
    private Comment parentComment;  // 자기참조 외래 키

    @Column(nullable = false, name = "comment_text")
    private String commentText;

    @Column(nullable = false, name = "created_at")
    private String createdAt;

    @Column(nullable = false, name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
