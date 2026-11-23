package com.suntravels.backend.service;

import com.suntravels.backend.dto.RoomRequirementRequest;
import com.suntravels.backend.dto.SearchRequest;
import com.suntravels.backend.dto.SearchResultResponse;
import com.suntravels.backend.entity.Contract;
import com.suntravels.backend.entity.Hotel;
import com.suntravels.backend.entity.RoomType;
import com.suntravels.backend.repository.BookingRepository;
import com.suntravels.backend.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Unit tests for search pricing and availability rules.
 * Easy to explain in viva: mock DB, assert formula + Available flag.
 */
@ExtendWith( MockitoExtension.class )
class SearchServiceTest
{
    @Mock
    private ContractRepository contractRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private SearchService searchService;

    private Contract hiltonContract;

    @BeforeEach
    void setUp()
    {
        Hotel hotel = new Hotel();
        hotel.setName( "Hilton Colombo" );

        RoomType standard = new RoomType();
        standard.setId( 1L );
        standard.setName( "Standard" );
        standard.setPricePerPersonPerNight( new BigDecimal( "35.00" ) );
        standard.setNumberOfRooms( 15 );
        standard.setMaxAdults( 2 );

        RoomType seaView = new RoomType();
        seaView.setId( 2L );
        seaView.setName( "Sea View" );
        seaView.setPricePerPersonPerNight( new BigDecimal( "55.00" ) );
        seaView.setNumberOfRooms( 8 );
        seaView.setMaxAdults( 3 );

        hiltonContract = new Contract();
        hiltonContract.setHotel( hotel );
        hiltonContract.setValidFrom( LocalDate.of( 2012, 1, 1 ) );
        hiltonContract.setValidTo( LocalDate.of( 2012, 12, 31 ) );
        hiltonContract.setMarkup( new BigDecimal( "1.150" ) );
        hiltonContract.setRoomTypes( List.of( standard, seaView ) );
        standard.setContract( hiltonContract );
        seaView.setContract( hiltonContract );
    }

    @Test
    void calculatesMarkedUpPriceForOneRoom()
    {
        when( contractRepository.findCoveringStay( any(), any() ) )
                .thenReturn( List.of( hiltonContract ) );
        when( bookingRepository.countOverlappingRooms( anyLong(), any(), any() ) ).thenReturn( 0L );

        SearchRequest request = buildRequest( LocalDate.of( 2012, 6, 1 ), 2, List.of( 2 ) );
        List<SearchResultResponse> results = searchService.search( request );

        SearchResultResponse standard = findRoom( results, "Standard" );
        // 35 * 1.15 * 2 nights * 2 adults = 161.00
        assertEquals( new BigDecimal( "161.00" ), standard.getPrice() );
        assertEquals( "Available", standard.getAvailabilityStatus() );
        org.junit.jupiter.api.Assertions.assertNotNull( standard.getRoomTypeId() );
    }

    @Test
    void marksUnavailableWhenAdultsExceedMax()
    {
        when( contractRepository.findCoveringStay( any(), any() ) )
                .thenReturn( List.of( hiltonContract ) );
        when( bookingRepository.countOverlappingRooms( anyLong(), any(), any() ) ).thenReturn( 0L );

        SearchRequest request = buildRequest( LocalDate.of( 2012, 6, 1 ), 1, List.of( 3 ) );
        List<SearchResultResponse> results = searchService.search( request );

        SearchResultResponse standard = findRoom( results, "Standard" );
        SearchResultResponse seaView = findRoom( results, "Sea View" );

        assertEquals( "Not available", standard.getAvailabilityStatus() );
        assertEquals( "Available", seaView.getAvailabilityStatus() );
    }

    @Test
    void marksUnavailableWhenOverlappingBookingsFillInventory()
    {
        when( contractRepository.findCoveringStay( any(), any() ) )
                .thenReturn( List.of( hiltonContract ) );
        // Standard has 15 rooms; pretend 15 already booked for overlapping dates
        when( bookingRepository.countOverlappingRooms( org.mockito.ArgumentMatchers.eq( 1L ), any(), any() ) )
                .thenReturn( 15L );
        when( bookingRepository.countOverlappingRooms( org.mockito.ArgumentMatchers.eq( 2L ), any(), any() ) )
                .thenReturn( 0L );

        SearchRequest request = buildRequest( LocalDate.of( 2012, 6, 1 ), 2, List.of( 2 ) );
        List<SearchResultResponse> results = searchService.search( request );

        assertEquals( "Not available", findRoom( results, "Standard" ).getAvailabilityStatus() );
        assertEquals( "Available", findRoom( results, "Sea View" ).getAvailabilityStatus() );
    }

    @Test
    void returnsEmptyWhenNoContractCoversStay()
    {
        when( contractRepository.findCoveringStay( any(), any() ) )
                .thenReturn( List.of() );

        SearchRequest request = buildRequest( LocalDate.of( 2013, 1, 1 ), 2, List.of( 2 ) );
        List<SearchResultResponse> results = searchService.search( request );

        assertTrue( results.isEmpty() );
    }

    private SearchRequest buildRequest( LocalDate checkIn, int nights, List<Integer> adultsPerRoom )
    {
        SearchRequest request = new SearchRequest();
        request.setCheckInDate( checkIn );
        request.setNumberOfNights( nights );
        request.setRooms( adultsPerRoom.stream().map( adults -> {
            RoomRequirementRequest room = new RoomRequirementRequest();
            room.setAdults( adults );
            return room;
        } ).toList() );
        return request;
    }

    private SearchResultResponse findRoom( List<SearchResultResponse> results, String roomType )
    {
        return results.stream()
                .filter( row -> roomType.equals( row.getRoomType() ) )
                .findFirst()
                .orElseThrow();
    }
}
