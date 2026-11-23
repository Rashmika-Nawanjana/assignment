package com.suntravels.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * A room type offered under a contract.
 * Example: Standard — $35, 15 rooms, max 2 adults
 */
@Entity
@Table( name = "room_types" )
public class RoomType
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne( fetch = FetchType.LAZY, optional = false )
    @JoinColumn( name = "contract_id", nullable = false )
    private Contract contract;

    @Column( nullable = false, length = 100 )
    private String name;

    /** Hotelier price per person, per night (before markup). */
    @Column( name = "price_per_person_per_night", nullable = false, precision = 12, scale = 2 )
    private BigDecimal pricePerPersonPerNight;

    @Column( name = "number_of_rooms", nullable = false )
    private Integer numberOfRooms;

    @Column( name = "max_adults", nullable = false )
    private Integer maxAdults;

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public Contract getContract()
    {
        return contract;
    }

    public void setContract( Contract contract )
    {
        this.contract = contract;
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
