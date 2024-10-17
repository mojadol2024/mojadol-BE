package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.BoardRequestDto;
import com.gnu.mojadol.dto.BoardResponseDto;
import com.gnu.mojadol.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {
    BoardResponseDto writeBoard(BoardRequestDto boardRequestDto);
    Page<BoardResponseDto> listBoard(int page, int size);

    BoardResponseDto updateBoard(BoardRequestDto boardRequestDto);

    BoardResponseDto boardDetail(int boardSeq);
}
