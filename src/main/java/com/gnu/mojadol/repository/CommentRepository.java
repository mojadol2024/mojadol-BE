package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c WHERE c.board.boardSeq = :boardSeq")
    List<Comment> findByBoard_BoardSeq(@Param("boardSeq") int boardSeq); // 특정 게시글에 대한 댓글 조회
    @Query("SELECT c.board FROM Comment c WHERE c.user.userSeq = :userSeq")
    Page<Board> findBoardsByUserSeq(@Param("userSeq") int userSeq, Pageable pageable);
    Comment findByCommentSeq(int commentSeq);
}