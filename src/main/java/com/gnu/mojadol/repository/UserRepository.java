package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 사용자 정의 쿼리 메서드도 여기에 추가 가능
}
