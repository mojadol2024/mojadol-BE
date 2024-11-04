package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.PhotoRequestDto;
import com.gnu.mojadol.dto.PhotoResponseDto;
import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.Photo;
import com.gnu.mojadol.repository.BoardRepository;
import com.gnu.mojadol.repository.PhotoRepository;
import com.gnu.mojadol.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoServiceImpl implements PhotoService {


    private final PhotoRepository photoRepository;
    private final BoardRepository boardRepository;

    // PhotoRepository를 주입받아 초기화하는 생성자
    @Autowired
    public PhotoServiceImpl(PhotoRepository photoRepository, BoardRepository boardRepository) {
        this.photoRepository = photoRepository;
        this.boardRepository = boardRepository;
    }

    @Override
    public PhotoResponseDto savePhoto(PhotoRequestDto photoRequestDto) {
        Board board = boardRepository.findById(photoRequestDto.getBoardSeq())
                .orElseThrow(() -> new RuntimeException("Board not found"));

        // Photo 엔티티 생성 및 설정
        Photo photo = new Photo();
        photo.setBoard(board);
        photo.setFilePath(photoRequestDto.getFilePath());
        photo.setUploadDate(photoRequestDto.getUploadDate());

        // 데이터베이스에 저장
        Photo savedPhoto = photoRepository.save(photo);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return new PhotoResponseDto(
                savedPhoto.getPhotoSeq(),
                savedPhoto.getBoard().getBoardSeq(),
                savedPhoto.getFilePath(),
                savedPhoto.getUploadDate()
        );
    }

    @Override
    public PhotoResponseDto getPhotoById(int photoSeq) {
        return photoRepository.findById(photoSeq)
                .map(photo -> new PhotoResponseDto(
                        photo.getPhotoSeq(),
                        photo.getBoard().getBoardSeq(),
                        photo.getFilePath(),
                        photo.getUploadDate()))
                .orElseThrow(() -> new RuntimeException("Photo not found"));
    }

    @Override
    public List<PhotoResponseDto> getPhotosByBoardSeq(int boardSeq) {
        List<Photo> photos = photoRepository.findByBoardSeq(boardSeq);
        return photos.stream()
                .map(photo -> new PhotoResponseDto(
                        photo.getPhotoSeq(),
                        photo.getBoard().getBoardSeq(),
                        photo.getFilePath(),
                        photo.getUploadDate()))
                .collect(Collectors.toList());
    }

    @Override
    public void deletePhoto(int photoSeq) {
        photoRepository.deleteById(photoSeq);
    }
}

