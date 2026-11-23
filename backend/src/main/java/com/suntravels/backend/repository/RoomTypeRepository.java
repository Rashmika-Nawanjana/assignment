package com.suntravels.backend.repository;

import com.suntravels.backend.entity.RoomType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long>
{
    /** Locks the room-type row so concurrent bookings cannot oversell the same inventory. */
    @Lock( LockModeType.PESSIMISTIC_WRITE )
    @Query( "SELECT r FROM RoomType r WHERE r.id = :id" )
    Optional<RoomType> findByIdForUpdate( @Param( "id" ) Long id );
}
