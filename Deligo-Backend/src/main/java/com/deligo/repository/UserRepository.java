package com.deligo.repository;

import com.deligo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;
import com.deligo.entity.UserRole;
import java.util.List;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.passwordResetTokenHash = :tokenHash")
    Optional<User> findForPasswordReset(@Param("tokenHash") String tokenHash);
    List<User> findByRoleOrderByCreatedAtDesc(UserRole role);
    List<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByCreatedAtDesc(String name, String email);
}
