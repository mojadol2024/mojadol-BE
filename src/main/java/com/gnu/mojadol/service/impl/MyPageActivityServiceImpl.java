package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.UserRequestDto;
import com.gnu.mojadol.entity.Board;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.BoardRepository;
import com.gnu.mojadol.repository.CommentRepository;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.MyPageActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyPageActivityServiceImpl implements MyPageActivityService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<Board> myBoardList(int userSeq, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return boardRepository.findByUserSeqAndReportNot(userSeq, pageable);
    }

    public Page<Board> myCommentList(int userSeq, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return commentRepository.findBoardsByUserSeq(userSeq, pageable);
    }

    public void updateUser(UserRequestDto userRequestDto) {
        User user = userRepository.findByUserId(userRequestDto.getUserId());

        if (userRequestDto.getUserPw() != null) {
            user.setUserPw(passwordEncoder.encode(userRequestDto.getUserPw()));
        }
        if (userRequestDto.getNickName() != null) {
            user.setNickname(userRequestDto.getNickName());
        }
        if (userRequestDto.getMail() != null) {
            user.setMail(userRequestDto.getMail());
        }
        userRepository.save(user);
    }





}
