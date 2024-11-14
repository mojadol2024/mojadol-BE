package com.gnu.mojadol.repository;

import com.gnu.mojadol.dto.BoardUserSeqAndDogNameDto;
import com.gnu.mojadol.entity.Board;
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

}
