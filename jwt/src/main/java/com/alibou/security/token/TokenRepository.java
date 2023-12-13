package com.alibou.security.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
            SELECT t
            FROM Token t 
            INNER JOIN t.user u 
            WHERE u.id = :userId AND (t.expired = false OR t.revoked = false)
            """)
    List<Token> findAllValidTokensByUser(@Param("userId") Integer userId);

    Optional<Token> findByToken(String token);

}
