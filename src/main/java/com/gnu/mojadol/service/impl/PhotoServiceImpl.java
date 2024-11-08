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
    private final PhotoRepository photoRepository; // Photo 엔티티에 대한 CRUD 작업을 위한 리포지토리
    private final BoardRepository boardRepository; // Board 엔티티에 대한 CRUD 작업을 위한 리포지토리

    // PhotoRepository와 BoardRepository를 주입받아 초기화하는 생성자
    @Autowired
    public PhotoServiceImpl(PhotoRepository photoRepository, BoardRepository boardRepository) {
        this.photoRepository = photoRepository;
        this.boardRepository = boardRepository;
    }

    /**
     * 게시글에 사진을 저장하는 메서드
     * @param photoRequestDto 사진 정보가 담긴 DTO
     * @return 저장된 사진의 정보를 담고 있는 DTO
     */
    @Override
    public PhotoResponseDto savePhoto(PhotoRequestDto photoRequestDto) {
        // 요청된 boardSeq를 사용하여 Board 엔티티를 조회
        Board board = boardRepository.findById(photoRequestDto.getBoardSeq())
                .orElseThrow(() -> new RuntimeException("Board not found"));

        // Photo 엔티티 생성 및 설정
        Photo photo = new Photo();
        photo.setBoard(board); // 게시글과 연결
        photo.setFilePath(photoRequestDto.getFilePath()); // 사진 파일 경로 설정
        photo.setUploadDate(photoRequestDto.getUploadDate()); // 업로드 날짜 설정

        // 데이터베이스에 저장
        Photo savedPhoto = photoRepository.save(photo);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return new PhotoResponseDto(
                savedPhoto.getPhotoSeq(), // 사진 고유 식별자
                savedPhoto.getBoard().getBoardSeq(), // 게시글 식별자
                savedPhoto.getFilePath(), // 사진 파일 경로
                savedPhoto.getUploadDate() // 업로드 날짜
        );
    }

    /**
     * 사진 ID를 통해 사진 정보를 조회하는 메서드
     * @param photoSeq 사진의 고유 식별자
     * @return 해당 사진의 정보를 담고 있는 DTO
     */
    @Override
    public PhotoResponseDto getPhotoById(int photoSeq) {
        return photoRepository.findById(photoSeq)
                .map(photo -> new PhotoResponseDto(
                        photo.getPhotoSeq(), // 사진 고유 식별자
                        photo.getBoard().getBoardSeq(), // 게시글 식별자
                        photo.getFilePath(), // 사진 파일 경로
                        photo.getUploadDate())) // 업로드 날짜
                .orElseThrow(() -> new RuntimeException("Photo not found")); // 사진이 존재하지 않으면 예외 처리
    }

    /**
     * 특정 게시글에 연결된 모든 사진 정보를 조회하는 메서드
     * @param boardSeq 게시글의 고유 식별자
     * @return 해당 게시글에 연결된 사진들의 정보를 담고 있는 DTO 리스트
     */
    @Override
    public List<PhotoResponseDto> getPhotosByBoardSeq(int boardSeq) {
        List<Photo> photos = photoRepository.findByBoard_BoardSeq(boardSeq);
        return photos.stream()
                .map(photo -> new PhotoResponseDto(
                        photo.getPhotoSeq(), // 사진 고유 식별자
                        photo.getBoard().getBoardSeq(), // 게시글 식별자
                        photo.getFilePath(), // 사진 파일 경로
                        photo.getUploadDate())) // 업로드 날짜
                .collect(Collectors.toList()); // DTO 리스트로 변환하여 반환
    }

    /**
     * 사진을 삭제하는 메서드
     * @param photoSeq 삭제할 사진의 고유 식별자
     */
    @Override
    public void deletePhoto(int photoSeq) {
        photoRepository.deleteById(photoSeq); // 사진 ID를 사용하여 삭제
    }
}
