package com.suntravels.backend.service;

import com.suntravels.backend.dto.AvailableRoomsResponse;
import com.suntravels.backend.dto.BookingRequest;
import com.suntravels.backend.dto.BookingResponse;
import com.suntravels.backend.dto.PageResponse;
import com.suntravels.backend.entity.Booking;
import com.suntravels.backend.entity.BookingStatus;
import com.suntravels.backend.entity.Contract;
import com.suntravels.backend.entity.RoomType;
import com.suntravels.backend.entity.UserAccount;
import com.suntravels.backend.repository.BookingRepository;
import com.suntravels.backend.repository.RoomTypeRepository;
import com.suntravels.backend.repository.UserAccountRepository;
import com.suntravels.backend.util.RoomNumbers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class BookingService
{
    private final BookingRepository bookingRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final UserAccountRepository userAccountRepository;

    public BookingService( BookingRepository bookingRepository,
                           RoomTypeRepository roomTypeRepository,
                           UserAccountRepository userAccountRepository )
    {
        this.bookingRepository = bookingRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Lists free physical room numbers for a room type on the requested stay.
     */
    @Transactional( readOnly = true )
    public AvailableRoomsResponse availableRooms( Long roomTypeId, LocalDate checkIn, int nights )
    {
        if( nights < 1 )
        {
            throw new ResponseStatusException( BAD_REQUEST, "numberOfNights must be at least 1" );
        }

        RoomType roomType = roomTypeRepository.findById( roomTypeId )
                .orElseThrow( () -> new ResponseStatusException( NOT_FOUND, "Room type not found" ) );

        Contract contract = roomType.getContract();
        LocalDate lastNight = checkIn.plusDays( nights - 1L );
        LocalDate exclusiveCheckOut = checkIn.plusDays( nights );

        if( checkIn.isBefore( contract.getValidFrom() ) || lastNight.isAfter( contract.getValidTo() ) )
        {
            throw new ResponseStatusException( BAD_REQUEST, "Stay is outside the contract valid period" );
        }

        AvailableRoomsResponse response = new AvailableRoomsResponse();
        response.setRoomTypeId( roomType.getId() );
        response.setRoomTypeName( roomType.getName() );
        response.setTotalRooms( roomType.getNumberOfRooms() );
        response.setAvailableRoomNumbers( freeRoomNumbers( roomType, checkIn, exclusiveCheckOut ) );
        return response;
    }

    /**
     * Books one physical room after search.
     * Guest picks a room number; overlapping confirmed stays on that number are rejected.
     */
    @Transactional
    public BookingResponse create( String username, BookingRequest request )
    {
        UserAccount agent = userAccountRepository.findByUsername( username )
                .orElseThrow( () -> new ResponseStatusException( NOT_FOUND, "User not found" ) );

        // Lock inventory row until this transaction commits (blocks parallel oversell)
        RoomType roomType = roomTypeRepository.findByIdForUpdate( request.getRoomTypeId() )
                .orElseThrow( () -> new ResponseStatusException( NOT_FOUND, "Room type not found" ) );

        Contract contract = roomType.getContract();
        LocalDate checkIn = request.getCheckInDate();
        int nights = request.getNumberOfNights();
        int adults = request.getAdults();
        String roomNumber = request.getRoomNumber() == null ? "" : request.getRoomNumber().trim();
        LocalDate lastNight = checkIn.plusDays( nights - 1L );
        LocalDate exclusiveCheckOut = checkIn.plusDays( nights );

        if( checkIn.isBefore( contract.getValidFrom() ) || lastNight.isAfter( contract.getValidTo() ) )
        {
            throw new ResponseStatusException( BAD_REQUEST, "Stay is outside the contract valid period" );
        }
        if( adults > roomType.getMaxAdults() )
        {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Adults exceed max adults (" + roomType.getMaxAdults() + ") for this room type"
            );
        }
        if( !RoomNumbers.isValid( roomNumber, roomType.getNumberOfRooms() ) )
        {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Room number must be between 1 and " + roomType.getNumberOfRooms()
            );
        }

        long taken = bookingRepository.countOverlappingForRoomNumber(
                roomType.getId(),
                roomNumber,
                checkIn,
                exclusiveCheckOut
        );
        if( taken > 0 )
        {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Room " + roomNumber + " is already booked for these dates"
            );
        }

        BigDecimal totalPrice = roomType.getPricePerPersonPerNight()
                .multiply( contract.getMarkup() )
                .multiply( BigDecimal.valueOf( nights ) )
                .multiply( BigDecimal.valueOf( adults ) )
                .setScale( 2, RoundingMode.HALF_UP );

        Booking booking = new Booking();
        booking.setBookedBy( agent );
        booking.setRoomType( roomType );
        booking.setHotelName( contract.getHotel().getName() );
        booking.setRoomTypeName( roomType.getName() );
        booking.setRoomNumber( roomNumber );
        booking.setCheckInDate( checkIn );
        booking.setNumberOfNights( nights );
        booking.setRoomsRequested( 1 );
        booking.setTotalAdults( adults );
        booking.setTotalPrice( totalPrice );
        booking.setGuestName( request.getGuestName().trim() );
        booking.setGuestNic( request.getGuestNic().trim() );
        booking.setGuestPhone( blankToNull( request.getGuestPhone() ) );
        booking.setGuestEmail( blankToNull( request.getGuestEmail() ) );
        booking.setStatus( BookingStatus.CONFIRMED );
        booking.setCreatedAt( Instant.now() );

        return toResponse( bookingRepository.save( booking ) );
    }

    @Transactional( readOnly = true )
    public PageResponse<BookingResponse> list( String query, int page, int size )
    {
        String q = query == null ? "" : query.trim();
        PageRequest pageable = PageRequest.of( page, size, Sort.by( "createdAt" ).descending() );
        Page<Booking> result = q.isEmpty()
                ? bookingRepository.findAll( pageable )
                : bookingRepository.search( q, pageable );
        return new PageResponse<>( result.map( this::toResponse ) );
    }

    @Transactional
    public BookingResponse cancel( Long id )
    {
        Booking booking = bookingRepository.findById( id )
                .orElseThrow( () -> new ResponseStatusException( NOT_FOUND, "Booking not found" ) );
        if( booking.getStatus() == BookingStatus.CANCELLED )
        {
            throw new ResponseStatusException( BAD_REQUEST, "Booking is already cancelled" );
        }
        booking.setStatus( BookingStatus.CANCELLED );
        return toResponse( bookingRepository.save( booking ) );
    }

    private List<String> freeRoomNumbers( RoomType roomType, LocalDate checkIn, LocalDate exclusiveCheckOut )
    {
        Set<String> booked = new HashSet<>( bookingRepository.findBookedRoomNumbers(
                roomType.getId(),
                checkIn,
                exclusiveCheckOut
        ) );
        List<String> free = RoomNumbers.forInventory( roomType.getNumberOfRooms() ).stream()
                .filter( number -> !booked.contains( number ) )
                .toList();

        // Legacy confirmed bookings without room numbers still reserve capacity
        long unassigned = bookingRepository.countOverlappingUnassigned(
                roomType.getId(),
                checkIn,
                exclusiveCheckOut
        );
        if( unassigned <= 0 || free.isEmpty() )
        {
            return free;
        }
        int keep = Math.max( 0, free.size() - (int) unassigned );
        return free.subList( 0, keep );
    }

    private String blankToNull( String value )
    {
        if( value == null || value.isBlank() )
        {
            return null;
        }
        return value.trim();
    }

    private BookingResponse toResponse( Booking booking )
    {
        BookingResponse response = new BookingResponse();
        response.setId( booking.getId() );
        response.setHotelName( booking.getHotelName() );
        response.setRoomTypeName( booking.getRoomTypeName() );
        response.setRoomNumber( booking.getRoomNumber() );
        response.setCheckInDate( booking.getCheckInDate() );
        response.setNumberOfNights( booking.getNumberOfNights() );
        response.setRoomsRequested( booking.getRoomsRequested() );
        response.setTotalAdults( booking.getTotalAdults() );
        response.setTotalPrice( booking.getTotalPrice() );
        response.setGuestName( booking.getGuestName() );
        response.setGuestNic( booking.getGuestNic() );
        response.setGuestPhone( booking.getGuestPhone() );
        response.setGuestEmail( booking.getGuestEmail() );
        response.setStatus( booking.getStatus() );
        response.setBookedBy( booking.getBookedBy().getUsername() );
        response.setCreatedAt( booking.getCreatedAt() );
        return response;
    }
}
