package com.gnu.mojadol.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnu.mojadol.dto.*;
import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.Photo;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.BoardRepository;
import com.gnu.mojadol.repository.PhotoRepository;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.*;
import com.gnu.mojadol.service.impl.FCMServiceImpl;
import com.gnu.mojadol.utils.JwtUtil;
import com.google.api.Http;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    @Autowired
    private AiService aiService;

    // Board 해야할 api 정리
    // Board main페이지 게시글 뿌려주기 10개 씩 페이징해서
    @GetMapping("/list")
    public ResponseEntity<?> listBoard(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size, @RequestParam(value = "breedName",required = false) String breedName,
                                       @RequestParam(value = "province", required = false) String province) {
        System.out.println("BoardController listBoard " + new Date());

        Page<Board> response = boardService.listBoard(page, size, breedName, province);

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
            boardMap.put("location", board.getLocation().getProvince() + " " + board.getLocation().getCity());
            boardMap.put("photo", "http://10.0.2.2:3000/images/uploads\\" + board.getPhoto().get(0).getFilePath());

            responseMap.add(boardMap);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("content", responseMap);
        map.put("pagination", Map.of(
                "totalPages", response.getTotalPages(),
                "totalElements", response.getTotalElements(),
                "currentPage", response.getNumber(),
                "pageSize", response.getSize()
        ));

        System.out.println(map);

        return ResponseEntity.ok(map);
    }
    // Board 글쓰기
    @PostMapping("/write")
    public ResponseEntity<String> write(@RequestParam(value = "file") List<MultipartFile> file,
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

            String directoryPath = "C:\\Users\\lovee\\uploads\\";
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();  // 디렉토리 생성 시 오류가 발생할 수 있음
                if (!created) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("디렉토리 생성에 실패했습니다.");
                }
            }
            for (MultipartFile multipartFile : file) {
                String originalFilename = multipartFile.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".") ?
                        originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
                String filePath = UUID.randomUUID().toString() + extension;
                String path = directoryPath + filePath;

                multipartFile.transferTo(new File(path));

                PhotoRequestDto photoRequestDto = new PhotoRequestDto();
                photoRequestDto.setFilePath(filePath);
                photoRequestDto.setBoardSeq(boardResponseDto.getBoardSeq());
                photoService.savePhoto(photoRequestDto);
            }

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
    public ResponseEntity<?> update(@ModelAttribute("data") String data,
                                    @RequestHeader("Authorization") String accessToken,
                                    @RequestParam(value = "file") List<MultipartFile> file) {
        System.out.println("BoardController update" + new Date());
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("파일을 반드시 업로드해야 합니다.");
        }
        try {
            System.out.println(data);
            // json형식의 data dto에 매핑
            ObjectMapper objectMapper = new ObjectMapper();
            BoardRequestDto boardRequestDto = objectMapper.readValue(data, BoardRequestDto.class);
            // 토큰에서 id추출
            String userId = jwtUtil.extractUsername(accessToken);
            User user = userRepository.findByUserId(userId);
            boardRequestDto.setUserSeq(user.getUserSeq());
            // 게시글 업데이트
            BoardResponseDto boardResponseDto = boardService.updateBoard(boardRequestDto);
            // 사진 flag 1로 변경
            photoService.deletePhoto(boardResponseDto.getBoardSeq());

            String directoryPath = "/Users/byeongyeongtae/uploads/";

            // 사진 저장
            for (MultipartFile multipartFile : file) {
                String originalFilename = multipartFile.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".") ?
                        originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
                String filePath = UUID.randomUUID().toString() + extension;
                String path = directoryPath + filePath;

                multipartFile.transferTo(new File(path));

                PhotoRequestDto photoRequestDto = new PhotoRequestDto();
                photoRequestDto.setFilePath(filePath);
                photoRequestDto.setBoardSeq(boardResponseDto.getBoardSeq());
                photoService.savePhoto(photoRequestDto);
            }

            return ResponseEntity.ok("YES");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/boardDetail")
    public ResponseEntity<?> boardDetail(@RequestParam int boardSeq, @RequestHeader("Authorization") String accessToken) {
        System.out.println("BoardController boardDetail" + new Date());

        List<CommentResponseDto> commentResponseDtos = commentService.getCommentsByBoardSeq(boardSeq);

        BoardResponseDto responseDto = boardService.boardDetail(boardSeq);

        Map<String, Object> responseMap = new HashMap<>();

        List<String> url = new ArrayList<>();
        for (String photo :responseDto.getPhotos()) {
            url.add("http://10.0.2.2:3000/images/uploads/" + photo);
        }
        responseDto.setPhotos(url);
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

    @PostMapping("/model")
    public ResponseEntity<?> model(@RequestHeader("Authorization") String accessToken, @RequestParam(value = "file") List<MultipartFile> files) {
        try {


            // AI 서비스 호출
            String response = aiService.getPrediction(files);

            // 예측 결과를 반환
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NO");
        }
    }
}
