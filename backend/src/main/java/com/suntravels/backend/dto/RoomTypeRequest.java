package com.suntravels.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RoomTypeRequest
{
    @NotBlank
    private String name;

    @NotNull
    @DecimalMin( "0.01" )
    private BigDecimal pricePerPersonPerNight;

    @NotNull
    @Min( 1 )
    private Integer numberOfRooms;

    @NotNull
    @Min( 1 )
    private Integer maxAdults;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public BigDecimal getPricePerPersonPerNight()
    {
        return pricePerPersonPerNight;
    }

    public void setPricePerPersonPerNight( BigDecimal pricePerPersonPerNight )
    {
        this.pricePerPersonPerNight = pricePerPersonPerNight;
    }

    public Integer getNumberOfRooms()
    {
        return numberOfRooms;
    }

    public void setNumberOfRooms( Integer numberOfRooms )
    {
        this.numberOfRooms = numberOfRooms;
    }

    public Integer getMaxAdults()
    {
        return maxAdults;
    }

    public void setMaxAdults( Integer maxAdults )
    {
        this.maxAdults = maxAdults;
    }
}
