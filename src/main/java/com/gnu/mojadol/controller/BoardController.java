package com.gnu.mojadol.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnu.mojadol.dto.*;
import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.BoardRepository;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.BoardService;
import com.gnu.mojadol.service.CommentService;
import com.gnu.mojadol.service.FCMService;
import com.gnu.mojadol.service.PhotoService;
import com.gnu.mojadol.service.impl.FCMServiceImpl;
import com.gnu.mojadol.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private FCMService fcmService;

    @Autowired
    private PhotoService photoService;

    // Board 해야할 api 정리
    // Board main페이지 게시글 뿌려주기 10개 씩 페이징해서
    @GetMapping("/list")
    public ResponseEntity<?> listBoard(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String breedName,
                                       @RequestParam(required = false) String dogName, @RequestParam(required = false) String location) {
        System.out.println("BoardController listBoard " + new Date());

        Page<Board> response = boardService.listBoard(page, size, breedName, dogName, location);

        List<Map<String, Object>> responseMap = new ArrayList<>();

        List<Board> content = response.getContent();
        for (Board board : content) {
            Map<String, Object> boardMap = new HashMap<>();
            // Board 객체의 값을 Map에 담기
            boardMap.put("boardSeq", board.getBoardSeq());
            boardMap.put("dogName", board.getDogName());
            boardMap.put("dogAge", board.getDogAge());
            boardMap.put("dogGender", board.getDogGender());
            boardMap.put("dogWeight", board.getDogWeight());
            boardMap.put("lostDate", board.getLostDate());
            boardMap.put("postDate", board.getPostDate());
            boardMap.put("memo", board.getMemo());
            boardMap.put("breedName", board.getBreed().getBreedName());
            boardMap.put("location", board.getLocation().getProvince() + board.getLocation().getCity());
            responseMap.add(boardMap);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("pagination", Map.of(
                "totalPages", response.getTotalPages(),
                "totalElements", response.getTotalElements(),
                "currentPage", response.getNumber(),
                "pageSize", response.getSize()
        ));

        return ResponseEntity.ok(map);
    }
    // Board 글쓰기
    @PostMapping("/write")
    public ResponseEntity<String> write(@RequestParam(value = "file") MultipartFile file,
                                        @ModelAttribute("data") String data,
                                        @RequestHeader("Authorization") String accessToken) {
        System.out.println("BaordController write" + new Date());
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("파일을 반드시 업로드해야 합니다.");
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BoardRequestDto boardRequestDto = objectMapper.readValue(data, BoardRequestDto.class);
            String userId = jwtUtil.extractUsername(accessToken);
            User user = userRepository.findByUserId(userId);
            boardRequestDto.setUserSeq(user.getUserSeq());
            BoardResponseDto boardResponseDto = boardService.writeBoard(boardRequestDto);

            String directoryPath = "/Users/byeongyeongtae/uploads/";
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();  // 디렉토리 생성 시 오류가 발생할 수 있음
                if (!created) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("디렉토리 생성에 실패했습니다.");
                }
            }

            String filePath = directoryPath + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            PhotoRequestDto photoRequestDto = new PhotoRequestDto();
            photoRequestDto.setFilePath(filePath); // Set file path in PhotoRequestDto
            photoRequestDto.setBoardSeq(boardResponseDto.getBoardSeq());
            PhotoResponseDto responseDto = photoService.savePhoto(photoRequestDto);

            if (boardRequestDto.getReport() == 1) {
                List<BoardUserSeqAndDogNameDto> boards = boardRepository.findUserSeqByBreedName(boardRequestDto.getBreedName());
                List<Integer> userSeqs = new ArrayList<>();
                List<String> bodies = new ArrayList<>();
                String title = "제보";
                for (BoardUserSeqAndDogNameDto board : boards) {
                    String body = board.getDogName() + "와 같은 견종이 제보 되었습니다!";
                    bodies.add(body);
                    userSeqs.add(board.getUserSeq());
                }
                fcmService.sendNotificationToUsers(userSeqs, title, bodies);
            }

            return ResponseEntity.ok("YES");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
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

        List<CommentResponseDto> commentResponseDtos = commentService.getCommentsByBoardSeq(boardSeq);

        BoardResponseDto responseDto = boardService.boardDetail(boardSeq);

        Map<String, Object> responseMap = new HashMap<>();

        responseMap.put("boardDetail", responseDto);
        responseMap.put("comments", commentResponseDtos);

        return ResponseEntity.ok(responseMap);

    }
    // Board 글삭제(delete가 아닌 안보이게 수정)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String accessToken, @RequestBody BoardRequestDto boardRequestDto) {
        System.out.println("BoardController delete" + new Date());

        String userId = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUserId(userId);
        boardRequestDto.setUserSeq(user.getUserSeq());
        String response = boardService.delete(boardRequestDto);

        return ResponseEntity.ok(response);
    }
}
