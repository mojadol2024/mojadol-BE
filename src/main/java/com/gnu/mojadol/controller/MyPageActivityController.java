package com.gnu.mojadol.controller;

import com.gnu.mojadol.config.SecurityConfig;
import com.gnu.mojadol.dto.UserRequestDto;
import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.MyPageActivityService;
import com.gnu.mojadol.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/myActivity")
public class MyPageActivityController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyPageActivityService myPageActivityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/myBoardList")
    public ResponseEntity<?> myBoardList(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestHeader("Authorization") String accessToken){
        System.out.println("MyPageActivityController myBoardList" + new Date());
        System.out.println(page);
        try {
            String userId = jwtUtil.extractUsername(accessToken);
            User user = userRepository.findByUserId(userId);

            Page<Board> response = myPageActivityService.myBoardList(user.getUserSeq(), page, size);

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
                boardMap.put("photo", "http://10.0.2.2:3000/images/uploads/" + board.getPhoto().get(0).getFilePath());

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
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("NO");
        }
    }

    @GetMapping("/myCommentList")
    public ResponseEntity<?> myCommentList(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestHeader("Authorization") String accessToken) {
        System.out.println("MyPageActivityController myCommentList" + new Date());
        try {
            String userId = jwtUtil.extractUsername(accessToken);
            User user = userRepository.findByUserId(userId);

            Page<Board> response = myPageActivityService.myCommentList(user.getUserSeq(), page, size);

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
                boardMap.put("photo", "http://10.0.2.2:3000/images/uploads/" + board.getPhoto().get(0).getFilePath());

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

            return ResponseEntity.ok(map);

        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("NO");
        }
    }

    @PostMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody UserRequestDto userRequestDto) {
        System.out.println("MyPageActivityController updateUser" + new Date());
        try {
            myPageActivityService.updateUser(userRequestDto);
            return ResponseEntity.ok("YES");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NO");
        }
    }

    //내 정보 수정 전 비밀번호 확인
    @PostMapping("/passwordCheck")
    public ResponseEntity<?> passwordCheck(@RequestBody UserRequestDto userRequestDto, @RequestHeader("Authorization") String accessToken) {
        System.out.println("MyPageActivityController passwordCheck" + new Date());
        try{
            String userId = jwtUtil.extractUsername(accessToken);
            User user = userRepository.findByUserId(userId);
            System.out.println(userRequestDto.getUserPw());
            if (passwordEncoder.matches(userRequestDto.getUserPw(), user.getUserPw())) {
                return ResponseEntity.ok("YES");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NO");
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NO");
        }
    }

    // 내정보 수정페이지 get으로 내정보 보여주기
    @GetMapping("/userData")
    public ResponseEntity<?> userDate(@RequestHeader("Authorization") String accessToken) {
        System.out.println("MyPageActivityController userDate" + new Date());
        try {
            String userId = jwtUtil.extractUsername(accessToken);
            User user = userRepository.findByUserId(userId);

            UserRequestDto response = new UserRequestDto();

            response.setUserId(user.getUserId());
            response.setMail(user.getMail());
            response.setUserName(user.getUsername());
            response.setNickName(user.getNickname());
            response.setPhoneNumber(user.getPhoneNumber());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NO");
        }
    }

}
