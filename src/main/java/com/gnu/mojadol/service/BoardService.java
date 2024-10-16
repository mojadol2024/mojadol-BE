package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.BoardRequestDto;
import com.gnu.mojadol.dto.BoardResponseDto;

import java.util.List;

public interface BoardService {
    List<BoardResponseDto> listBoard(BoardRequestDto boardRequestDto);
    BoardResponseDto writeBoard(BoardRequestDto boardRequestDto);
}
