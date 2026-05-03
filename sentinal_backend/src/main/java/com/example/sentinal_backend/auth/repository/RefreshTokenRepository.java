package com.example.sentinal_backend.auth.repository;

import com.example.sentinal_backend.user.model.AppUser;
import com.example.sentinal_backend.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(AppUser user);
}
