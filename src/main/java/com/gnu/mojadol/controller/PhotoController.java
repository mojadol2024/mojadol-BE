package com.gnu.mojadol.controller;

import com.gnu.mojadol.dto.PhotoResponseDto;
import com.gnu.mojadol.dto.PhotoRequestDto;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.PhotoService;
import com.gnu.mojadol.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/Photo")
public class PhotoController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserRepository userRepository;

    // 사진 저장
    @PostMapping("/save")
    public ResponseEntity<PhotoResponseDto> savePhoto(@RequestBody PhotoRequestDto photoRequestDto, @RequestHeader("Authorization") String accessToken) {
        String userId = jwtUtil.extractUsername(accessToken);
        System.out.println("Extracted userId: " + userId); // 확인용 로그 추가

        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //photoRequestDto.setUserSeq(user.getUserSeq());
        PhotoResponseDto photoResponseDto = photoService.savePhoto(photoRequestDto);

        return ResponseEntity.ok(photoResponseDto);
    }

    // 사진 삭제
    @PostMapping("/delete") // get은 client에서 받을 값이 없을때 post는 값을 받아올때
    public ResponseEntity<String> deletePhoto(@RequestBody int photoSeq, @RequestHeader("Authorization") String accessToken) {
        System.out.println("PhotoController deletePhoto " + new Date());

        // JWT 토큰에서 사용자 ID 추출
        String userId = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUserId(userId);

        /*
        // 삭제하려는 사진이 존재하는지, 그리고 해당 사진의 소유자가 요청 사용자와 일치하는지 확인
        PhotoResponseDto photo = photoService.getPhotoById(photoSeq);
        if (photo.getUserSeq() != user.getUserSeq()) {
            // 사용자가 사진의 소유자가 아닐 경우 접근 권한이 없음을 알림
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this photo.");
        }
*/
        // 소유자가 맞을 경우 삭제 수행
        photoService.deletePhoto(photoSeq);

        return ResponseEntity.ok("YES");
    }

    // 어떤 게시글에 대한 모든 사진 조회
    @GetMapping("/list/{boardSeq}")
    public ResponseEntity<List<PhotoResponseDto>> listPhotos(@PathVariable int boardSeq) {
        System.out.println("PhotoController listPhotos " + new Date());

        List<PhotoResponseDto> photoResponseDto = photoService.getPhotosByBoardSeq(boardSeq);
        return ResponseEntity.ok(photoResponseDto);
    }
}
