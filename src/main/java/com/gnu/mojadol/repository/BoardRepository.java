package com.gnu.mojadol.repository;

import com.gnu.mojadol.dto.BoardUserSeqAndDogNameDto;
import com.gnu.mojadol.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer>, JpaSpecificationExecutor<Board> {
    Board findByBoardSeq(int boardSeq);
    @Query("SELECT new com.gnu.mojadol.dto.BoardUserSeqAndDogNameDto(b.user.userSeq, b.dogName) " +
            "FROM Board b WHERE b.breed.breedName = :breedName and b.report = 0")
    List<BoardUserSeqAndDogNameDto> findUserSeqByBreedName(@Param("breedName")String breedName);

    @Query(value = "SELECT distinct b FROM Board b WHERE b.report != 2 order by b.postDate desc ")
    Page<Board> findBoards(Specification spec, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.report != 2 AND b.user.userSeq = :userSeq order by b.postDate desc ")
    Page<Board> findByUserSeqAndReportNot(@Param("userSeq") int userSeq, Pageable pageable);



}
