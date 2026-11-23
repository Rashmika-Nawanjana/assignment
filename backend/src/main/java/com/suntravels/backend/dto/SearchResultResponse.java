package com.suntravels.backend.dto;

import java.math.BigDecimal;

/**
 * One search result row shown to the call-center agent.
 */
public class SearchResultResponse
{
    private Long roomTypeId;
    private String hotelName;
    private String roomType;
    private BigDecimal price;
    private String availabilityStatus;

    public SearchResultResponse()
    {
    }

    public SearchResultResponse( Long roomTypeId,
                                 String hotelName,
                                 String roomType,
                                 BigDecimal price,
                                 String availabilityStatus )
    {
        this.roomTypeId = roomTypeId;
        this.hotelName = hotelName;
        this.roomType = roomType;
        this.price = price;
        this.availabilityStatus = availabilityStatus;
    }

    public Long getRoomTypeId()
    {
        return roomTypeId;
    }

    public void setRoomTypeId( Long roomTypeId )
    {
        this.roomTypeId = roomTypeId;
    }

    public String getHotelName()
    {
        return hotelName;
    }

    public void setHotelName( String hotelName )
    {
        this.hotelName = hotelName;
    }

    public String getRoomType()
    {
        return roomType;
    }

    public void setRoomType( String roomType )
    {
        this.roomType = roomType;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice( BigDecimal price )
    {
        this.price = price;
    }

    public String getAvailabilityStatus()
    {
        return availabilityStatus;
    }

    public void setAvailabilityStatus( String availabilityStatus )
    {
        this.availabilityStatus = availabilityStatus;
    }
}
