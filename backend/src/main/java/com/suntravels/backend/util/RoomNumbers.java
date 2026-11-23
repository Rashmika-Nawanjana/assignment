package com.suntravels.backend.util;

import java.util.ArrayList;
import java.util.List;

/** Builds physical room numbers 1..N for a room type inventory. */
public final class RoomNumbers
{
    private RoomNumbers()
    {
    }

    public static List<String> forInventory( int numberOfRooms )
    {
        List<String> numbers = new ArrayList<>( Math.max( numberOfRooms, 0 ) );
        for( int i = 1; i <= numberOfRooms; i++ )
        {
            numbers.add( String.valueOf( i ) );
        }
        return numbers;
    }

    public static boolean isValid( String roomNumber, int numberOfRooms )
    {
        if( roomNumber == null || roomNumber.isBlank() )
        {
            return false;
        }
        try
        {
            int value = Integer.parseInt( roomNumber.trim() );
            return value >= 1 && value <= numberOfRooms;
        }
        catch( NumberFormatException ex )
        {
            return false;
        }
    }
}
