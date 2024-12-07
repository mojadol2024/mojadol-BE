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

    @Query("SELECT b FROM Board b " +
            "JOIN b.breed br " +
            "JOIN b.location loc " +
            "WHERE b.report != 2 " +
            "AND (:breedName IS NULL OR br.breedName LIKE %:breedName%) " +
            "AND (:province IS NULL OR loc.province LIKE %:province%) " +
            "ORDER BY b.postDate DESC")
    Page<Board> findSearchBoards(
            @Param("breedName") String breedName,
            @Param("province") String province,
            Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "WHERE b.report != 2 " +
            "ORDER BY b.postDate DESC")
    Page<Board> findBoards(Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.report != 2 AND b.user.userSeq = :userSeq order by b.postDate desc ")
    Page<Board> findByUserSeqAndReportNot(@Param("userSeq") int userSeq, Pageable pageable);



}
