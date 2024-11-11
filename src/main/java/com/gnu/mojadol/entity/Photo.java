package com.gnu.mojadol.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PHOTO")
@Data
@NoArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_seq")
    private int photoSeq;  // 기본 키

    // @ManyToOne
    // @JoinColumn(name = "user_seq", referencedColumnName = "user_seq", nullable = false)
    // private User user;  // USER 테이블의 외래 키로 사용자와 관계 설정

    @ManyToOne
    @JoinColumn(name = "board_seq", referencedColumnName = "board_seq", nullable = false)
    private Board board;  // BOARD 외래 키

    @Column(nullable = false, name = "file_path")
    private String filePath;  // 파일 경로

    @Column(nullable = true, name = "upload_date")
    private String uploadDate;  // 업로드 날짜
}
