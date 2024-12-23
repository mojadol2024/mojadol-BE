package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserId(String userName);
    User findByUserSeq(int userSeq);
    User findByMail(String mail);
    User findByUserIdAndMail(String userId, String mail);
}
