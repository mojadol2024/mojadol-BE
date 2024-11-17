package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c WHERE c.deletedFlag = 0 AND c.board.boardSeq = :boardSeq")
    List<Comment> findByBoard_BoardSeq(@Param("boardSeq") int boardSeq); // 특정 게시글에 대한 댓글 조회
    List<Comment> findByUser_UserSeq(int userSeq); // 사용자 ID로 댓글 찾기
    Comment findByCommentSeq(int commentSeq);
}