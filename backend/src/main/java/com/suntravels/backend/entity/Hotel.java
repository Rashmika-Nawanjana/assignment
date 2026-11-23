package com.suntravels.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * A hotel that provides rooms through contracts.
 * Example: Hilton Colombo
 */
@Entity
@Table( name = "hotels" )
public class Hotel
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column( nullable = false, length = 150 )
    private String name;

    /** One hotel can have many contracts (different date ranges). */
    @OneToMany( mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Contract> contracts = new ArrayList<>();

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

    public List<Contract> getContracts()
    {
        return contracts;
    }

    public void setContracts( List<Contract> contracts )
    {
        this.contracts = contracts;
    }
}
