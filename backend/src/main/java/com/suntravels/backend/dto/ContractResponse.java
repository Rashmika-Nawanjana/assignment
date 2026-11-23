package com.suntravels.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContractResponse
{
    private Long id;
    private String hotelName;
    private LocalDate validFrom;
    private LocalDate validTo;
    private BigDecimal markup;
    private List<RoomTypeResponse> roomTypes = new ArrayList<>();

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

    public LocalDate getValidFrom()
    {
        return validFrom;
    }

    public void setValidFrom( LocalDate validFrom )
    {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo()
    {
        return validTo;
    }

    public void setValidTo( LocalDate validTo )
    {
        this.validTo = validTo;
    }

    public BigDecimal getMarkup()
    {
        return markup;
    }

    public void setMarkup( BigDecimal markup )
    {
        this.markup = markup;
    }

    public List<RoomTypeResponse> getRoomTypes()
    {
        return roomTypes;
    }

    public void setRoomTypes( List<RoomTypeResponse> roomTypes )
    {
        this.roomTypes = roomTypes;
    }
}
