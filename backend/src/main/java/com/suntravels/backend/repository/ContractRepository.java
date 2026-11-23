package com.suntravels.backend.repository;

import com.suntravels.backend.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long>
{
    @Query( """
            SELECT DISTINCT c FROM Contract c
            JOIN FETCH c.hotel
            JOIN FETCH c.roomTypes
            WHERE c.validFrom <= :checkIn
              AND c.validTo >= :lastNight
            """ )
    List<Contract> findCoveringStay( @Param( "checkIn" ) LocalDate checkIn,
                                     @Param( "lastNight" ) LocalDate lastNight );

    @Query( """
            SELECT c FROM Contract c
            JOIN c.hotel h
            WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :q, '%'))
            """ )
    Page<Contract> searchByHotelName( @Param( "q" ) String q, Pageable pageable );
}
