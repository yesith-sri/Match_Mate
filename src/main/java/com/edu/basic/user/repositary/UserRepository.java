package com.edu.basic.user.repositary;

import com.edu.basic.repositary.CommonRepository;
import com.edu.basic.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CommonRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndIsActiveTrue(Long id);
}