package com.spring.repo;

import com.spring.model.Status;
import com.spring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Integer> {
        Optional<User> findByUserName(String userName);

        boolean existsByUserEmail(String userEmail);

        boolean existsByPhoneNumber(String phoneNumber);

        Optional<User> findByUserEmail(String userEmail);

        @Query("SELECT u FROM User u WHERE u.userEmail = :email AND u.userStatus = 'ACTIVE'")
        Optional<User> findActiveUserByEmail(@Param("email") String email);

        List<User> findByUserStatus(Status userStatus);
}
