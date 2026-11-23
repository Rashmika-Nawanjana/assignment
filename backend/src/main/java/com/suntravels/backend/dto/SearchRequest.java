package com.suntravels.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Call-center search criteria.
 */
public class SearchRequest
{
    @NotNull
    private LocalDate checkInDate;

    @NotNull
    @Min( 1 )
    private Integer numberOfNights;

    @NotEmpty
    @Valid
    private List<RoomRequirementRequest> rooms;

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

    public List<RoomRequirementRequest> getRooms()
    {
        return rooms;
    }

    public void setRooms( List<RoomRequirementRequest> rooms )
    {
        this.rooms = rooms;
    }
}
