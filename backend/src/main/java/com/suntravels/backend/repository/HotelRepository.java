package com.suntravels.backend.repository;

import com.suntravels.backend.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long>
{
    Optional<Hotel> findByNameIgnoreCase( String name );

    long countByNameStartingWith( String prefix );
}
