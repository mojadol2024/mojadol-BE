package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    //List<Board> findByUser_UserSeq(int userSeq); // 사용자 ID로 게시글 찾기
    Page<Board> findAllByReportNot(int report, Pageable pageable);
    Board findByBoardSeq(int boardSeq);
}
