package com.suntravels.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Book one room after search.
 * Guest identity details are collected at booking time (not during search).
 */
public class BookingRequest
{
    @NotNull
    private Long roomTypeId;

    /** Physical room within the type, e.g. "1" … numberOfRooms */
    @NotBlank
    @Size( max = 20 )
    private String roomNumber;

    @NotNull
    private LocalDate checkInDate;

    @NotNull
    @Min( 1 )
    private Integer numberOfNights;

    /** Adults staying in this single room booking */
    @NotNull
    @Min( 1 )
    private Integer adults;

    @NotBlank
    @Size( max = 120 )
    private String guestName;

    @NotBlank
    @Size( max = 30 )
    private String guestNic;

    @Size( max = 30 )
    private String guestPhone;

    @Email
    @Size( max = 120 )
    private String guestEmail;

    public Long getRoomTypeId()
    {
        return roomTypeId;
    }

    public void setRoomTypeId( Long roomTypeId )
    {
        this.roomTypeId = roomTypeId;
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

    public Integer getAdults()
    {
        return adults;
    }

    public void setAdults( Integer adults )
    {
        this.adults = adults;
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
}
