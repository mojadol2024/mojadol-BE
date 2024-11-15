package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.MyPageActivityDto;
import com.gnu.mojadol.entity.MyPageActivity;
import com.gnu.mojadol.repository.MyPageActivityRepository;
import com.gnu.mojadol.service.MyPageActivityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyPageActivityServiceImpl implements MyPageActivityService {

    private final MyPageActivityRepository myPageActivityRepository;

    public MyPageActivityServiceImpl(MyPageActivityRepository myPageActivityRepository) {
        this.myPageActivityRepository = myPageActivityRepository;
    }

    @Override
    public Page<MyPageActivityDto> getUserActivities(int userSeq, Pageable pageable) {
        return myPageActivityRepository.findByUser_UserSeq(userSeq, pageable)
                .map(this::toDto);
    }

    @Override
    public Page<MyPageActivityDto> getUserPosts(int userSeq, Pageable pageable) {
        return myPageActivityRepository.findByUser_UserSeqAndActivityType(userSeq, "POST", pageable)
                .map(this::toDto);
    }

    @Override
    public Page<MyPageActivityDto> getUserComments(int userSeq, Pageable pageable) {
        return myPageActivityRepository.findByUser_UserSeqAndActivityType(userSeq, "COMMENT", pageable)
                .map(this::toDto);
    }

    @Override
    public void deleteUserActivity(Long activityId) {
        Optional<MyPageActivity> activityOptional = myPageActivityRepository.findById(activityId);
        if (activityOptional.isPresent()) {
            MyPageActivity activity = activityOptional.get();
            activity.setDeletedFlag(1); // 논리 삭제
            myPageActivityRepository.save(activity);
        } else {
            throw new IllegalArgumentException("Invalid activity ID: " + activityId);
        }
    }

    @Override
    public MyPageActivityDto getActivityDetail(Long activityId) {
        MyPageActivity activity = myPageActivityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid activity ID: " + activityId));
        return toDto(activity);
    }

    private MyPageActivityDto toDto(MyPageActivity activity) {
        return new MyPageActivityDto(
                activity.getBoard().getBoardSeq(),
                activity.getUser().getUserSeq(),
                activity.getDogName(),
                activity.getBreedName(),
                activity.getLostDate(),
                activity.getCreatedAt(),
                activity.getUpdatedAt(),
                activity.getActivityType(),
                activity.getParentBoardSeq() != null ? activity.getParentBoardSeq().getBoardSeq() : 0,
                activity.getDeletedFlag()
        );
    }
}
