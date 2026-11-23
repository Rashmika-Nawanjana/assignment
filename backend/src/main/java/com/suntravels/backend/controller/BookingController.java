package com.suntravels.backend.controller;

import com.suntravels.backend.dto.AvailableRoomsResponse;
import com.suntravels.backend.dto.BookingRequest;
import com.suntravels.backend.dto.BookingResponse;
import com.suntravels.backend.dto.PageResponse;
import com.suntravels.backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping( "/api/bookings" )
public class BookingController
{
    private final BookingService bookingService;

    public BookingController( BookingService bookingService )
    {
        this.bookingService = bookingService;
    }

    @GetMapping( "/available-rooms" )
    public AvailableRoomsResponse availableRooms(
            @RequestParam Long roomTypeId,
            @RequestParam @DateTimeFormat( iso = DateTimeFormat.ISO.DATE ) LocalDate checkInDate,
            @RequestParam int numberOfNights )
    {
        return bookingService.availableRooms( roomTypeId, checkInDate, numberOfNights );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    public BookingResponse create( Authentication authentication,
                                   @Valid @RequestBody BookingRequest request )
    {
        return bookingService.create( authentication.getName(), request );
    }

    @GetMapping
    public PageResponse<BookingResponse> list( @RequestParam( defaultValue = "" ) String q,
                                               @RequestParam( defaultValue = "0" ) int page,
                                               @RequestParam( defaultValue = "8" ) int size )
    {
        return bookingService.list( q, page, size );
    }

    @PutMapping( "/{id}/cancel" )
    public BookingResponse cancel( @PathVariable Long id )
    {
        return bookingService.cancel( id );
    }
}
