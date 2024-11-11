package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.BoardRequestDto;
import com.gnu.mojadol.dto.BoardResponseDto;
import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.Breed;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.BoardRepository;
import com.gnu.mojadol.repository.BreedRepository;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  BoardRepository boardRepository;

    @Autowired
    private BreedRepository breedRepository;


    public BoardResponseDto writeBoard(BoardRequestDto boardRequestDto) {
        User user = null;
        if (boardRequestDto != null) {
            user = userRepository.findByUserSeq(boardRequestDto.getUserSeq());

            if (user == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다."); // 예외 처리
            }
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateString = dateFormat.format(date);

        Breed breed = breedRepository.findById(boardRequestDto.getBreedName()).orElse(null);

        Board board = new Board();
        board.setDogName(boardRequestDto.getDogName());
        board.setDogAge(boardRequestDto.getDogAge());
        board.setDogGender(boardRequestDto.getDogGender());
        board.setDogWeight(boardRequestDto.getDogWeight());
        board.setLostDate(boardRequestDto.getLostDate());
        board.setPostDate(dateString);
        board.setMemo(boardRequestDto.getMemo());
        board.setBreed(breed);
        board.setUser(user);

        Board savedBoard = boardRepository.save(board);

        BoardResponseDto responseDto = new BoardResponseDto();
        responseDto.setBoardSeq(savedBoard.getBoardSeq());
        responseDto.setDogName(savedBoard.getDogName());
        responseDto.setDogAge(savedBoard.getDogAge());
        responseDto.setDogGender(savedBoard.getDogGender());
        responseDto.setDogWeight(savedBoard.getDogWeight());
        responseDto.setLostDate(savedBoard.getLostDate());
        responseDto.setPostDate(savedBoard.getPostDate());
        responseDto.setMemo(savedBoard.getMemo());

        return responseDto;
    }

    public Page<BoardResponseDto> listBoard(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boards = boardRepository.findAllByReportNot(2, pageable); // report가 2인 값은 삭제된 글

        // Board 엔티티를 BoardResponseDto로 변환
        Page<BoardResponseDto> dtoPage = boards.map(board -> {
            BoardResponseDto dto = new BoardResponseDto();
            dto.setBoardSeq(board.getBoardSeq());
            dto.setDogName(board.getDogName());
            dto.setDogAge(board.getDogAge());
            dto.setDogGender(board.getDogGender());
            dto.setDogWeight(board.getDogWeight());
            dto.setLostDate(board.getLostDate());
            dto.setPostDate(board.getPostDate());
            dto.setMemo(board.getMemo());
            dto.setBreedName(board.getBreed().getBreedName()); // Breed 이름
            dto.setNickName(board.getUser().getNickname());    // 사용자 닉네임만 노출
            return dto;  // 변환된 DTO 반환
        });

        return dtoPage;  // 변환된 Page<BoardResponseDto> 반환
    }

    public BoardResponseDto updateBoard(BoardRequestDto boardRequestDto) {
        if (boardRequestDto != null) {
            Board board = boardRepository.findById(boardRequestDto.getBoardSeq())
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

            User user = userRepository.findByUserSeq(boardRequestDto.getUserSeq());

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateString = dateFormat.format(date);

        board.setDogName(boardRequestDto.getDogName());
        board.setDogAge(boardRequestDto.getDogAge());
        board.setDogGender(boardRequestDto.getDogGender());
        board.setDogWeight(boardRequestDto.getDogWeight());
        board.setLostDate(boardRequestDto.getLostDate());
        board.setMemo(boardRequestDto.getMemo());

        Board updatedBoard = boardRepository.save(board);

        BoardResponseDto boardResponseDto = new BoardResponseDto();

        boardResponseDto.setBoardSeq(updatedBoard.getBoardSeq());
        boardResponseDto.setDogName(updatedBoard.getDogName());
        boardResponseDto.setDogAge(updatedBoard.getDogAge());
        boardResponseDto.setDogGender(updatedBoard.getDogGender());
        boardResponseDto.setDogWeight(updatedBoard.getDogWeight());
        boardResponseDto.setLostDate(updatedBoard.getLostDate());
        boardResponseDto.setMemo(updatedBoard.getMemo());
        boardResponseDto.setBreedName(updatedBoard.getBreed().getBreedName());
        boardResponseDto.setUserSeq(boardRequestDto.getUserSeq());
        boardResponseDto.setPostDate(dateString);
        boardResponseDto.setNickName(user.getNickname());

        return boardResponseDto;
        }
        throw new IllegalArgumentException("유효하지 않는 요청입니다.");
    }

    public BoardResponseDto boardDetail(int boardSeq) {
        if (boardSeq != 0) {
            Board board = boardRepository.findById(boardSeq)
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

            User user = userRepository.findByUserSeq(board.getUser().getUserSeq());

            BoardResponseDto responseDto = new BoardResponseDto();
            responseDto.setNickName(user.getNickname());
            responseDto.setUserSeq(user.getUserSeq());
            responseDto.setBoardSeq(board.getBoardSeq());
            responseDto.setDogGender(board.getDogGender());
            responseDto.setReport(board.getReport());
            responseDto.setMemo(board.getMemo());
            responseDto.setDogName(board.getDogName());
            responseDto.setDogAge(board.getDogAge());
            responseDto.setDogWeight(board.getDogWeight());
            responseDto.setLostDate(board.getLostDate());
            responseDto.setPostDate(board.getPostDate());
            responseDto.setBreedName(board.getBreed().getBreedName());


            return responseDto;
        }
        throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
    }


    public String delete(BoardRequestDto boardRequestDto) {
        if (boardRequestDto != null) {
            Board board = boardRepository.findById(boardRequestDto.getBoardSeq())
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

            User user = userRepository.findByUserSeq(boardRequestDto.getUserSeq());

            board.setReport(2);

            boardRepository.save(board);

            return "YES";
        }
        throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
    }
}