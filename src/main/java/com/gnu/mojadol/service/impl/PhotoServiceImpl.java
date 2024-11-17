package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.PhotoRequestDto;
import com.gnu.mojadol.dto.PhotoResponseDto;
import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.Photo;
import com.gnu.mojadol.repository.BoardRepository;
import com.gnu.mojadol.repository.PhotoRepository;
import com.gnu.mojadol.service.PhotoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public PhotoServiceImpl(PhotoRepository photoRepository, BoardRepository boardRepository) {
        this.photoRepository = photoRepository;
        this.boardRepository = boardRepository;
    }

    @Override
    public PhotoResponseDto savePhoto(PhotoRequestDto photoRequestDto) {
        Board board = boardRepository.findById(photoRequestDto.getBoardSeq())
                .orElseThrow(() -> new RuntimeException("Board not found"));

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);

        Photo photo = new Photo();
        photo.setBoard(board);
        photo.setFilePath(photoRequestDto.getFilePath());
        photo.setUploadDate(dateString);

        Photo savedPhoto = photoRepository.save(photo);

        return new PhotoResponseDto(
                savedPhoto.getPhotoSeq(),
                savedPhoto.getBoard().getBoardSeq(),
                savedPhoto.getFilePath(),
                savedPhoto.getUploadDate()
        );
    }

    @Override
    public PhotoResponseDto getPhotoById(int photoSeq) {
        Photo photo = photoRepository.findById(photoSeq)
                .orElseThrow(() -> new RuntimeException("Photo not found with ID: " + photoSeq));

        return new PhotoResponseDto(
                photo.getPhotoSeq(),
                // photo.getUser().getUserSeq(), // 사용자 식별자
                photo.getBoard().getBoardSeq(),
                photo.getFilePath(),
                photo.getUploadDate()
        );
    }

    @Override
    public List<PhotoResponseDto> getPhotosByBoardSeq(int boardSeq) {
        List<Photo> photos = photoRepository.findByBoard_BoardSeq(boardSeq);
        return photos.stream()
                .map(photo -> new PhotoResponseDto(
                        photo.getPhotoSeq(),
                        // photo.getBoard().getUser().getUserSeq(), // 사용자 식별자
                        photo.getBoard().getBoardSeq(),
                        photo.getFilePath(),
                        photo.getUploadDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePhoto(int boardSeq) {
        photoRepository.deleteFlagUpdate(boardSeq);
    }
}
