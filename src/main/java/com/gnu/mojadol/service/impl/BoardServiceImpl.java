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

    @Override
    public List<BoardResponseDto> listBoard(BoardRequestDto boardRequestDto) {

        return null;
    }
}
