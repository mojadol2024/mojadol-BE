package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.BoardRequestDto;
import com.gnu.mojadol.dto.BoardResponseDto;
import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.Breed;
import com.gnu.mojadol.entity.Location;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.BoardRepository;
import com.gnu.mojadol.repository.BreedRepository;
import com.gnu.mojadol.repository.LocationRepository;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  BoardRepository boardRepository;

    @Autowired
    private BreedRepository breedRepository;

    @Autowired
    private LocationRepository locationRepository;


    public BoardResponseDto writeBoard(BoardRequestDto boardRequestDto) {
        User user = null;
        if (boardRequestDto != null) {
            user = userRepository.findByUserSeq(boardRequestDto.getUserSeq());

            if (user == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다."); // 예외 처리
            }
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateString = dateFormat.format(date);

        Breed breed = breedRepository.findById(boardRequestDto.getBreedName())
                .orElseThrow(() -> new IllegalArgumentException("Breed가 없습니다"));

        Location newLocation = new Location();
        newLocation.setProvince(boardRequestDto.getProvince());
        newLocation.setCity(boardRequestDto.getCity());
        newLocation.setDistrict(boardRequestDto.getDistrict());
        locationRepository.save(newLocation);

        Board board = new Board();
        board.setDogName(boardRequestDto.getDogName());
        board.setDogAge(boardRequestDto.getDogAge());
        board.setDogGender(boardRequestDto.getDogGender());
        board.setDogWeight(boardRequestDto.getDogWeight());
        board.setLostDate(boardRequestDto.getLostDate());
        board.setPostDate(dateString);
        board.setMemo(boardRequestDto.getMemo());
        board.setBreed(breed);
        board.setUser(user);
        board.setLocation(newLocation);

        Board savedBoard = boardRepository.save(board);

        BoardResponseDto responseDto = new BoardResponseDto();
        responseDto.setBoardSeq(savedBoard.getBoardSeq());
        responseDto.setDogName(savedBoard.getDogName());
        responseDto.setDogAge(savedBoard.getDogAge());
        responseDto.setDogGender(savedBoard.getDogGender());
        responseDto.setDogWeight(savedBoard.getDogWeight());
        responseDto.setLostDate(savedBoard.getLostDate());
        responseDto.setPostDate(savedBoard.getPostDate());
        responseDto.setMemo(savedBoard.getMemo());

        return responseDto;
    }
    // 견종 or 개이름 까지는 완성  위치 검색을 논의 해봐야 할듯 어떻게 값이 들어오게 할건지 의논해야함
    public Page<Board> listBoard(int page, int size, String breedName, String dogName, String location) {
        Pageable pageable = PageRequest.of(page, size);

        // 조건을 설정하는 Specification 생성
        Specification<Board> spec = Specification.where((root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("report"), 2));

        if (breedName != null && !breedName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("breedName"), "%" + breedName + "%"));
        }

        if (dogName != null && !dogName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("dogName"), "%" + dogName + "%"));
        }

        if (location != null && !location.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("location"), "%" + location + "%"));
        }

        Page<Board> boards = boardRepository.findAll(spec, pageable);

        return boards;
    }

    public BoardResponseDto updateBoard(BoardRequestDto boardRequestDto) {
        if (boardRequestDto != null) {
            Board board = boardRepository.findById(boardRequestDto.getBoardSeq())
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

            User user = userRepository.findByUserSeq(boardRequestDto.getUserSeq());
            Location location = locationRepository.findById(board.getLocation().getLocationSeq())
                    .orElseThrow(() -> new IllegalArgumentException("위치 정보가 존재하지 않습니다."));

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String dateString = dateFormat.format(date);

            location.setProvince(boardRequestDto.getProvince());
            location.setCity(boardRequestDto.getCity());
            location.setDistrict(boardRequestDto.getDistrict());

            locationRepository.save(location);

            board.setDogName(boardRequestDto.getDogName());
            board.setDogAge(boardRequestDto.getDogAge());
            board.setDogGender(boardRequestDto.getDogGender());
            board.setDogWeight(boardRequestDto.getDogWeight());
            board.setLostDate(boardRequestDto.getLostDate());
            board.setMemo(boardRequestDto.getMemo());

            Board updatedBoard = boardRepository.save(board);

            BoardResponseDto boardResponseDto = new BoardResponseDto();

            boardResponseDto.setBoardSeq(updatedBoard.getBoardSeq());
            boardResponseDto.setDogName(updatedBoard.getDogName());
            boardResponseDto.setDogAge(updatedBoard.getDogAge());
            boardResponseDto.setDogGender(updatedBoard.getDogGender());
            boardResponseDto.setDogWeight(updatedBoard.getDogWeight());
            boardResponseDto.setLostDate(updatedBoard.getLostDate());
            boardResponseDto.setMemo(updatedBoard.getMemo());
            boardResponseDto.setBreedName(updatedBoard.getBreed().getBreedName());
            boardResponseDto.setUserSeq(boardRequestDto.getUserSeq());
            boardResponseDto.setPostDate(dateString);
            boardResponseDto.setNickName(user.getNickname());

        return boardResponseDto;
        }
        throw new IllegalArgumentException("유효하지 않는 요청입니다.");
    }

    public BoardResponseDto boardDetail(int boardSeq) {
        if (boardSeq != 0) {
            Board board = boardRepository.findById(boardSeq)
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

            User user = userRepository.findByUserSeq(board.getUser().getUserSeq());

            BoardResponseDto responseDto = new BoardResponseDto();
            responseDto.setNickName(user.getNickname());
            responseDto.setUserSeq(user.getUserSeq());
            responseDto.setBoardSeq(board.getBoardSeq());
            responseDto.setDogGender(board.getDogGender());
            responseDto.setReport(board.getReport());
            responseDto.setMemo(board.getMemo());
            responseDto.setDogName(board.getDogName());
            responseDto.setDogAge(board.getDogAge());
            responseDto.setDogWeight(board.getDogWeight());
            responseDto.setLostDate(board.getLostDate());
            responseDto.setPostDate(board.getPostDate());
            responseDto.setBreedName(board.getBreed().getBreedName());


            return responseDto;
        }
        throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
    }

    public String delete(BoardRequestDto boardRequestDto) {
        if (boardRequestDto != null) {
            Board board = boardRepository.findById(boardRequestDto.getBoardSeq())
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

            User user = userRepository.findByUserSeq(boardRequestDto.getUserSeq());

            board.setReport(2);

            boardRepository.save(board);

            return "YES";
        }
        throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
    }

/*
    // 시군 까지 받기
    public BoardRequestDto addressParser(String location) {
        String pattern = "(경상남도|경기도|서울특별시|부산광역시|대구광역시|인천광역시|광주광역시|대전광역시|울산광역시|세종특별자치시|" +
                "[가-힣]+도|[가-힣]+시|[가-힣]+군|[가-힣]+구)([가-힣]+시|[가-힣]+구)([가-힣]+동|[가-힣]+읍|[가-힣]+면)";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(location);

        if(m.find()) {
            String province = m.group(1);
            String city = m.group(2);
            String district = m.group(3);

            BoardRequestDto request = new BoardRequestDto();
            request.setProvince(province);
            request.setCity(city);
            request.setDistrict(district);

            return request;
        }else{
            throw new IllegalArgumentException("존재하지 않는 지역입니다.");
        }
    }
*/

}