package com.suntravels.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * One room the customer wants, with how many adults stay in it.
 * Example: 1 room with 2 adults
 */
public class RoomRequirementRequest
{
    @NotNull
    @Min( 1 )
    private Integer adults;

    public Integer getAdults()
    {
        return adults;
    }

    public void setAdults( Integer adults )
    {
        this.adults = adults;
    }
}
