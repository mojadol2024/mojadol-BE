package com.gnu.mojadol.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "PHOTO")
@Data
@NoArgsConstructor
@ToString(exclude = "board")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_seq")
    private int photoSeq;

    @ManyToOne
    @JoinColumn(name = "board_seq", referencedColumnName = "board_seq", nullable = false)
    private Board board;

    @Column(nullable = false, name = "file_path")
    private String filePath;

    @Column(nullable = true, name = "upload_date")
    private String uploadDate;

    @Column(nullable = false, name = "deleted_flag", columnDefinition = "TINYINT DEFAULT 0")
    private int deletedFlag = 0;
}
