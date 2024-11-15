
package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    // 특정 게시글에 포함된 모든 사진을 가져 오는 메소드
    List<Photo> findByBoard_BoardSeq(int boardSeq);
}