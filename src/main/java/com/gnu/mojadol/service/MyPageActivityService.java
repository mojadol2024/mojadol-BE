package com.gnu.mojadol.service;

import com.gnu.mojadol.dto.BoardResponseDto;
import com.gnu.mojadol.dto.UserRequestDto;
import com.gnu.mojadol.entity.Board;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MyPageActivityService {

    Page<Board> myBoardList(int userSeq, int page, int size);

    Page<Board> myCommentList(int userSeq, int page, int size);

    void updateUser(UserRequestDto userRequestDto);
}
