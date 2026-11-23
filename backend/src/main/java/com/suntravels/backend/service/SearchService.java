package com.suntravels.backend.service;

import com.suntravels.backend.dto.RoomRequirementRequest;
import com.suntravels.backend.dto.SearchRequest;
import com.suntravels.backend.dto.SearchResultResponse;
import com.suntravels.backend.entity.Contract;
import com.suntravels.backend.entity.RoomType;
import com.suntravels.backend.repository.BookingRepository;
import com.suntravels.backend.repository.ContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Search logic for call-center agents.
 *
 * Price = hotelPrice * markup * nights * adults (summed for each requested room)
 * Available = free rooms for the stay dates (contract rooms minus overlapping confirmed bookings)
 *            AND every requested room's adults <= maxAdults
 * Stay must fall fully inside the contract valid period.
 */
@Service
public class SearchService
{
    private final ContractRepository contractRepository;
    private final BookingRepository bookingRepository;

    public SearchService( ContractRepository contractRepository,
                          BookingRepository bookingRepository )
    {
        this.contractRepository = contractRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional( readOnly = true )
    public List<SearchResultResponse> search( SearchRequest request )
    {
        LocalDate checkIn = request.getCheckInDate();
        int nights = request.getNumberOfNights();
        List<RoomRequirementRequest> rooms = request.getRooms();

        // Last night of stay = check-in + (nights - 1)
        LocalDate lastNight = checkIn.plusDays( nights - 1L );
        LocalDate exclusiveCheckOut = checkIn.plusDays( nights );

        if( lastNight.isBefore( checkIn ) )
        {
            throw new ResponseStatusException( BAD_REQUEST, "numberOfNights must be at least 1" );
        }

        List<Contract> contracts = contractRepository.findCoveringStay( checkIn, lastNight );
        List<SearchResultResponse> results = new ArrayList<>();

        for( Contract contract : contracts )
        {
            for( RoomType roomType : contract.getRoomTypes() )
            {
                BigDecimal totalPrice = calculateTotalPrice( roomType, contract.getMarkup(), nights, rooms );
                long bookedRooms = bookingRepository.countOverlappingRooms(
                        roomType.getId(),
                        checkIn,
                        exclusiveCheckOut
                );
                int freeRooms = Math.max( 0, roomType.getNumberOfRooms() - (int) bookedRooms );
                String status = isAvailable( roomType, rooms, freeRooms ) ? "Available" : "Not available";

                results.add( new SearchResultResponse(
                        roomType.getId(),
                        contract.getHotel().getName(),
                        roomType.getName(),
                        totalPrice,
                        status
                ) );
            }
        }

        return results;
    }

    /**
     * Assignment formula, applied per requested room, then summed.
     * price * markup * nights * adults
     */
    private BigDecimal calculateTotalPrice( RoomType roomType,
                                            BigDecimal markup,
                                            int nights,
                                            List<RoomRequirementRequest> rooms )
    {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal nightsBd = BigDecimal.valueOf( nights );

        for( RoomRequirementRequest room : rooms )
        {
            BigDecimal adultsBd = BigDecimal.valueOf( room.getAdults() );
            BigDecimal roomPrice = roomType.getPricePerPersonPerNight()
                    .multiply( markup )
                    .multiply( nightsBd )
                    .multiply( adultsBd );
            total = total.add( roomPrice );
        }

        return total.setScale( 2, RoundingMode.HALF_UP );
    }

    /**
     * Available only if:
     * 1) enough free rooms remain after overlapping confirmed bookings
     * 2) every requested room's adult count fits under maxAdults
     */
    private boolean isAvailable( RoomType roomType, List<RoomRequirementRequest> rooms, int freeRooms )
    {
        if( rooms.size() > freeRooms )
        {
            return false;
        }

        for( RoomRequirementRequest room : rooms )
        {
            if( room.getAdults() > roomType.getMaxAdults() )
            {
                return false;
            }
        }

        return true;
    }
}
