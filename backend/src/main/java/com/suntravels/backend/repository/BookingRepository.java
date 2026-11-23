package com.suntravels.backend.repository;

import com.suntravels.backend.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long>
{
    @Query( """
            SELECT b FROM Booking b
            WHERE LOWER(b.hotelName) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(b.guestName) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(b.guestNic) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(b.roomTypeName) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(COALESCE(b.roomNumber, '')) LIKE LOWER(CONCAT('%', :q, '%'))
            """ )
    Page<Booking> search( @Param( "q" ) String q, Pageable pageable );

    /**
     * Confirmed rooms already taken for an overlapping stay.
     * Stay ranges use exclusive check-out = check-in + nights.
     */
    @Query( value = """
            SELECT COALESCE(SUM(rooms_requested), 0)
            FROM bookings
            WHERE room_type_id = :roomTypeId
              AND status = 'CONFIRMED'
              AND check_in_date < :exclusiveCheckOut
              AND DATE_ADD(check_in_date, INTERVAL number_of_nights DAY) > :checkIn
            """, nativeQuery = true )
    long countOverlappingRooms( @Param( "roomTypeId" ) Long roomTypeId,
                                 @Param( "checkIn" ) LocalDate checkIn,
                                 @Param( "exclusiveCheckOut" ) LocalDate exclusiveCheckOut );

    /** Room numbers already confirmed for an overlapping stay. */
    @Query( value = """
            SELECT room_number
            FROM bookings
            WHERE room_type_id = :roomTypeId
              AND status = 'CONFIRMED'
              AND room_number IS NOT NULL
              AND check_in_date < :exclusiveCheckOut
              AND DATE_ADD(check_in_date, INTERVAL number_of_nights DAY) > :checkIn
            """, nativeQuery = true )
    java.util.List<String> findBookedRoomNumbers( @Param( "roomTypeId" ) Long roomTypeId,
                                                   @Param( "checkIn" ) LocalDate checkIn,
                                                   @Param( "exclusiveCheckOut" ) LocalDate exclusiveCheckOut );

    @Query( value = """
            SELECT COUNT(*)
            FROM bookings
            WHERE room_type_id = :roomTypeId
              AND room_number = :roomNumber
              AND status = 'CONFIRMED'
              AND check_in_date < :exclusiveCheckOut
              AND DATE_ADD(check_in_date, INTERVAL number_of_nights DAY) > :checkIn
            """, nativeQuery = true )
    long countOverlappingForRoomNumber( @Param( "roomTypeId" ) Long roomTypeId,
                                         @Param( "roomNumber" ) String roomNumber,
                                         @Param( "checkIn" ) LocalDate checkIn,
                                         @Param( "exclusiveCheckOut" ) LocalDate exclusiveCheckOut );

    /** Older bookings without a room number still consume inventory. */
    @Query( value = """
            SELECT COALESCE(SUM(rooms_requested), 0)
            FROM bookings
            WHERE room_type_id = :roomTypeId
              AND status = 'CONFIRMED'
              AND room_number IS NULL
              AND check_in_date < :exclusiveCheckOut
              AND DATE_ADD(check_in_date, INTERVAL number_of_nights DAY) > :checkIn
            """, nativeQuery = true )
    long countOverlappingUnassigned( @Param( "roomTypeId" ) Long roomTypeId,
                                      @Param( "checkIn" ) LocalDate checkIn,
                                      @Param( "exclusiveCheckOut" ) LocalDate exclusiveCheckOut );
}
