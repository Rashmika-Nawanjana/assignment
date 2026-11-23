package com.suntravels.backend.dto;

import java.util.ArrayList;
import java.util.List;

/** Free physical room numbers for a room type on the requested stay. */
public class AvailableRoomsResponse
{
    private Long roomTypeId;
    private String roomTypeName;
    private int totalRooms;
    private List<String> availableRoomNumbers = new ArrayList<>();

    public Long getRoomTypeId()
    {
        return roomTypeId;
    }

    public void setRoomTypeId( Long roomTypeId )
    {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTypeName()
    {
        return roomTypeName;
    }

    public void setRoomTypeName( String roomTypeName )
    {
        this.roomTypeName = roomTypeName;
    }

    public int getTotalRooms()
    {
        return totalRooms;
    }

    public void setTotalRooms( int totalRooms )
    {
        this.totalRooms = totalRooms;
    }

    public List<String> getAvailableRoomNumbers()
    {
        return availableRoomNumbers;
    }

    public void setAvailableRoomNumbers( List<String> availableRoomNumbers )
    {
        this.availableRoomNumbers = availableRoomNumbers;
    }
}
