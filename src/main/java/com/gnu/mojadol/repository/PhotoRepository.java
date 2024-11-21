
package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    // 특정 게시글에 포함된 모든 사진을 가져 오는 메소드
    @Modifying
    @Query("UPDATE Photo p SET p.deletedFlag = 1 WHERE p.board.boardSeq = :boardSeq")
    void deleteFlagUpdate(@Param("boardSeq") int boardSeq);

    @Query("SELECT p FROM Photo p WHERE p.board.boardSeq = :boardSeq AND p.deletedFlag = 0")
    List<Photo> findByBoard_BoardSeq(@Param("boardSeq") int boardSeq);

}