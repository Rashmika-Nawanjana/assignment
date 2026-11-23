package com.suntravels.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Payload used by ADMIN to load a paper contract into the system.
 */
public class ContractRequest
{
    @NotBlank
    private String hotelName;

    @NotNull
    private LocalDate validFrom;

    @NotNull
    private LocalDate validTo;

    /** Multiplier, e.g. 1.15 = 15% markup. Defaults in service if null. */
    @DecimalMin( "1.0" )
    private BigDecimal markup;

    @NotEmpty
    @Valid
    private List<RoomTypeRequest> roomTypes;

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

    public List<RoomTypeRequest> getRoomTypes()
    {
        return roomTypes;
    }

    public void setRoomTypes( List<RoomTypeRequest> roomTypes )
    {
        this.roomTypes = roomTypes;
    }
}
