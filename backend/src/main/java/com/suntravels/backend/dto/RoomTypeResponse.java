package com.suntravels.backend.dto;

import java.math.BigDecimal;

public class RoomTypeResponse
{
    private Long id;
    private String name;
    private BigDecimal pricePerPersonPerNight;
    private Integer numberOfRooms;
    private Integer maxAdults;

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

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
