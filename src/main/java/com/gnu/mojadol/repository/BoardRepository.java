package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findAllByReportNot(int report, Pageable pageable);
    Board findByBoardSeq(int boardSeq);

}
