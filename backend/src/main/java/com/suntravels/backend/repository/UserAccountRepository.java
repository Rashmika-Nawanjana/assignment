package com.suntravels.backend.repository;

import com.suntravels.backend.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>
{
    Optional<UserAccount> findByUsername( String username );

    boolean existsByUsername( String username );

    @Query( """
            SELECT u FROM UserAccount u
            WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(u.fullName, '')) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :q, '%'))
            """ )
    Page<UserAccount> search( @Param( "q" ) String q, Pageable pageable );
}
