package com.suntravels.backend.dto;

import com.suntravels.backend.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class BookingResponse
{
    private Long id;
    private String hotelName;
    private String roomTypeName;
    private String roomNumber;
    private LocalDate checkInDate;
    private Integer numberOfNights;
    private Integer roomsRequested;
    private Integer totalAdults;
    private BigDecimal totalPrice;
    private String guestName;
    private String guestNic;
    private String guestPhone;
    private String guestEmail;
    private BookingStatus status;
    private String bookedBy;
    private Instant createdAt;

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public String getHotelName()
    {
        return hotelName;
    }

    public void setHotelName( String hotelName )
    {
        this.hotelName = hotelName;
    }

    public String getRoomTypeName()
    {
        return roomTypeName;
    }

    public void setRoomTypeName( String roomTypeName )
    {
        this.roomTypeName = roomTypeName;
    }

    public String getRoomNumber()
    {
        return roomNumber;
    }

    public void setRoomNumber( String roomNumber )
    {
        this.roomNumber = roomNumber;
    }

    public LocalDate getCheckInDate()
    {
        return checkInDate;
    }

    public void setCheckInDate( LocalDate checkInDate )
    {
        this.checkInDate = checkInDate;
    }

    public Integer getNumberOfNights()
    {
        return numberOfNights;
    }

    public void setNumberOfNights( Integer numberOfNights )
    {
        this.numberOfNights = numberOfNights;
    }

    public Integer getRoomsRequested()
    {
        return roomsRequested;
    }

    public void setRoomsRequested( Integer roomsRequested )
    {
        this.roomsRequested = roomsRequested;
    }

    public Integer getTotalAdults()
    {
        return totalAdults;
    }

    public void setTotalAdults( Integer totalAdults )
    {
        this.totalAdults = totalAdults;
    }

    public BigDecimal getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice( BigDecimal totalPrice )
    {
        this.totalPrice = totalPrice;
    }

    public String getGuestName()
    {
        return guestName;
    }

    public void setGuestName( String guestName )
    {
        this.guestName = guestName;
    }

    public String getGuestNic()
    {
        return guestNic;
    }

    public void setGuestNic( String guestNic )
    {
        this.guestNic = guestNic;
    }

    public String getGuestPhone()
    {
        return guestPhone;
    }

    public void setGuestPhone( String guestPhone )
    {
        this.guestPhone = guestPhone;
    }

    public String getGuestEmail()
    {
        return guestEmail;
    }

    public void setGuestEmail( String guestEmail )
    {
        this.guestEmail = guestEmail;
    }

    public BookingStatus getStatus()
    {
        return status;
    }

    public void setStatus( BookingStatus status )
    {
        this.status = status;
    }

    public String getBookedBy()
    {
        return bookedBy;
    }

    public void setBookedBy( String bookedBy )
    {
        this.bookedBy = bookedBy;
    }

    public Instant getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( Instant createdAt )
    {
        this.createdAt = createdAt;
    }
}
