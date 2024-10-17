package com.gnu.mojadol.controller;

import com.gnu.mojadol.dto.BoardRequestDto;
import com.gnu.mojadol.dto.BoardResponseDto;
import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.BoardService;
import com.gnu.mojadol.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserRepository userRepository;

    // Board 해야할 api 정리
    // Board main페이지 게시글 뿌려주기 10개 씩 페이징해서
    @GetMapping("/list")
    public ResponseEntity<?> listBoard(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        System.out.println("BoardController listBoard " + new Date());

        Page<BoardResponseDto> response = boardService.listBoard(page, size);


        return ResponseEntity.ok(response);
    }
    // Board 글쓰기
    @PostMapping("/write")
    public ResponseEntity<?> write(@RequestBody BoardRequestDto boardRequestDto, @RequestHeader("Authorization") String accessToken) {
        String userId = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUserId(userId);
        boardRequestDto.setUserSeq(user.getUserSeq());
        boardService.writeBoard(boardRequestDto);

        return ResponseEntity.ok(HttpStatus.OK);
    }
    // Board 글수정
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody BoardRequestDto boardRequestDto, @RequestHeader("Authorization") String accessToken) {
        System.out.println("BoardController update" + new Date());
        String userId = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUserId(userId);
        boardRequestDto.setUserSeq(user.getUserSeq());
        BoardResponseDto responseDto = boardService.updateBoard(boardRequestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/boardDetail")
    public ResponseEntity<?> boardDetail(@RequestParam int boardSeq) {
        System.out.println("BoardController boardDetail" + new Date());

        BoardResponseDto responseDto = boardService.boardDetail(boardSeq);

        return ResponseEntity.ok(responseDto);

    }
    // Board 글삭제(delete가 아닌 안보이게 수정)
}
