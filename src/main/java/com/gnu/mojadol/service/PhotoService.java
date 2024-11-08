package com.gnu.mojadol.service;
import com.gnu.mojadol.dto.PhotoRequestDto;
import com.gnu.mojadol.dto.PhotoResponseDto;
import com.gnu.mojadol.entity.Photo;
import java.util.List;
public interface PhotoService {
    // 사진 저장
    PhotoResponseDto savePhoto(PhotoRequestDto photoRequestDto);
    // ID로 사진 조회
    PhotoResponseDto getPhotoById(int photoSeq);
    // 어떤 게시글에 대한 모든 사진 조회
    List<PhotoResponseDto> getPhotosByBoardSeq(int boardSeq);
    // 사진 삭제
    void deletePhoto(int photoSeq);
}