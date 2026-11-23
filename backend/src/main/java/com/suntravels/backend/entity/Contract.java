package com.suntravels.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Paper contract loaded into the system.
 * Holds validity period, markup, and room types.
 */
@Entity
@Table( name = "contracts" )
public class Contract
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne( fetch = FetchType.LAZY, optional = false )
    @JoinColumn( name = "hotel_id", nullable = false )
    private Hotel hotel;

    @Column( name = "valid_from", nullable = false )
    private LocalDate validFrom;

    @Column( name = "valid_to", nullable = false )
    private LocalDate validTo;

    /**
     * Markup as a multiplier, e.g. 1.15 means 15% markup.
     * Price to customer = hotelPrice * markup * nights * adults
     */
    @Column( nullable = false, precision = 6, scale = 3 )
    private BigDecimal markup = new BigDecimal( "1.150" );

    @OneToMany( mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<RoomType> roomTypes = new ArrayList<>();

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public Hotel getHotel()
    {
        return hotel;
    }

    public void setHotel( Hotel hotel )
    {
        this.hotel = hotel;
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

    public List<RoomType> getRoomTypes()
    {
        return roomTypes;
    }

    public void setRoomTypes( List<RoomType> roomTypes )
    {
        this.roomTypes = roomTypes;
    }
}
