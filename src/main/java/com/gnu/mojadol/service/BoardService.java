package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.BoardRequestDto;
import com.gnu.mojadol.dto.BoardResponseDto;
import com.gnu.mojadol.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {
    BoardResponseDto writeBoard(BoardRequestDto boardRequestDto);
    Page<Board> listBoard(int page, int size, String title, String dogName, String location);

    BoardResponseDto updateBoard(BoardRequestDto boardRequestDto);

    BoardResponseDto boardDetail(int boardSeq);

    String delete(BoardRequestDto boardRequestDto);
}
