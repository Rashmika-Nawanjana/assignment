package com.suntravels.backend.service;

import com.suntravels.backend.dto.AvailableRoomsResponse;
import com.suntravels.backend.dto.BookingRequest;
import com.suntravels.backend.entity.Booking;
import com.suntravels.backend.entity.Contract;
import com.suntravels.backend.entity.Hotel;
import com.suntravels.backend.entity.Role;
import com.suntravels.backend.entity.RoomType;
import com.suntravels.backend.entity.UserAccount;
import com.suntravels.backend.repository.BookingRepository;
import com.suntravels.backend.repository.RoomTypeRepository;
import com.suntravels.backend.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
class BookingServiceTest
{
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private BookingService bookingService;

    private UserAccount agent;
    private RoomType standard;

    @BeforeEach
    void setUp()
    {
        agent = new UserAccount();
        agent.setId( 1L );
        agent.setUsername( "agent" );
        agent.setRole( Role.AGENT );

        Hotel hotel = new Hotel();
        hotel.setName( "Hilton Colombo" );

        Contract contract = new Contract();
        contract.setHotel( hotel );
        contract.setValidFrom( LocalDate.of( 2026, 1, 1 ) );
        contract.setValidTo( LocalDate.of( 2026, 12, 31 ) );
        contract.setMarkup( new BigDecimal( "1.150" ) );

        standard = new RoomType();
        standard.setId( 10L );
        standard.setName( "Standard" );
        standard.setPricePerPersonPerNight( new BigDecimal( "35.00" ) );
        standard.setNumberOfRooms( 2 );
        standard.setMaxAdults( 2 );
        standard.setContract( contract );
    }

    @Test
    void rejectsWhenSameRoomNumberAlreadyBookedForOverlap()
    {
        when( userAccountRepository.findByUsername( "agent" ) ).thenReturn( Optional.of( agent ) );
        when( roomTypeRepository.findByIdForUpdate( 10L ) ).thenReturn( Optional.of( standard ) );
        when( bookingRepository.countOverlappingForRoomNumber(
                eq( 10L ), eq( "1" ), any(), any() ) ).thenReturn( 1L );

        BookingRequest request = sampleRequest( "1" );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> bookingService.create( "agent", request )
        );
        assertTrue( ex.getReason().contains( "already booked" ) );
    }

    @Test
    void createsBookingWhenRoomNumberIsFree()
    {
        when( userAccountRepository.findByUsername( "agent" ) ).thenReturn( Optional.of( agent ) );
        when( roomTypeRepository.findByIdForUpdate( 10L ) ).thenReturn( Optional.of( standard ) );
        when( bookingRepository.countOverlappingForRoomNumber(
                eq( 10L ), eq( "1" ), any(), any() ) ).thenReturn( 0L );
        when( bookingRepository.save( any( Booking.class ) ) ).thenAnswer( inv -> {
            Booking b = inv.getArgument( 0 );
            b.setId( 99L );
            return b;
        } );

        var response = bookingService.create( "agent", sampleRequest( "1" ) );

        assertEquals( 99L, response.getId() );
        assertEquals( "1", response.getRoomNumber() );
        assertEquals( new BigDecimal( "80.50" ), response.getTotalPrice() );
        // 35 * 1.15 * 1 night * 2 adults = 80.50

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass( Booking.class );
        verify( bookingRepository ).save( captor.capture() );
        assertEquals( "1", captor.getValue().getRoomNumber() );
    }

    @Test
    void availableRoomsExcludesBookedNumbers()
    {
        when( roomTypeRepository.findById( 10L ) ).thenReturn( Optional.of( standard ) );
        when( bookingRepository.findBookedRoomNumbers( eq( 10L ), any(), any() ) )
                .thenReturn( List.of( "1" ) );
        when( bookingRepository.countOverlappingUnassigned( eq( 10L ), any(), any() ) )
                .thenReturn( 0L );

        AvailableRoomsResponse response = bookingService.availableRooms(
                10L,
                LocalDate.of( 2026, 6, 1 ),
                2
        );

        assertEquals( List.of( "2" ), response.getAvailableRoomNumbers() );
        assertEquals( 2, response.getTotalRooms() );
    }

    private BookingRequest sampleRequest( String roomNumber )
    {
        BookingRequest request = new BookingRequest();
        request.setRoomTypeId( 10L );
        request.setRoomNumber( roomNumber );
        request.setCheckInDate( LocalDate.of( 2026, 6, 1 ) );
        request.setNumberOfNights( 1 );
        request.setAdults( 2 );
        request.setGuestName( "Test Guest" );
        request.setGuestNic( "NIC123" );
        return request;
    }
}
