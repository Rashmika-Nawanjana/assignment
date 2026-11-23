package com.suntravels.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table( name = "bookings" )
public class Booking
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne( fetch = FetchType.LAZY, optional = false )
    @JoinColumn( name = "booked_by_id", nullable = false )
    private UserAccount bookedBy;

    @ManyToOne( fetch = FetchType.LAZY, optional = false )
    @JoinColumn( name = "room_type_id", nullable = false )
    private RoomType roomType;

    @Column( name = "hotel_name", nullable = false, length = 150 )
    private String hotelName;

    @Column( name = "room_type_name", nullable = false, length = 100 )
    private String roomTypeName;

    /** Physical room within the type, e.g. "1", "2" … numberOfRooms */
    @Column( name = "room_number", length = 20 )
    private String roomNumber;

    @Column( name = "check_in_date", nullable = false )
    private LocalDate checkInDate;

    @Column( name = "number_of_nights", nullable = false )
    private Integer numberOfNights;

    @Column( name = "rooms_requested", nullable = false )
    private Integer roomsRequested;

    @Column( name = "total_adults", nullable = false )
    private Integer totalAdults;

    @Column( name = "total_price", nullable = false, precision = 14, scale = 2 )
    private BigDecimal totalPrice;

    @Column( name = "guest_name", nullable = false, length = 120 )
    private String guestName;

    @Column( name = "guest_nic", length = 30 )
    private String guestNic;

    @Column( name = "guest_phone", length = 30 )
    private String guestPhone;

    @Column( name = "guest_email", length = 120 )
    private String guestEmail;

    @Enumerated( EnumType.STRING )
    @Column( nullable = false, length = 20 )
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column( name = "created_at", nullable = false )
    private Instant createdAt = Instant.now();

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public UserAccount getBookedBy()
    {
        return bookedBy;
    }

    public void setBookedBy( UserAccount bookedBy )
    {
        this.bookedBy = bookedBy;
    }

    public RoomType getRoomType()
    {
        return roomType;
    }

    public void setRoomType( RoomType roomType )
    {
        this.roomType = roomType;
    }

    public String getHotelName()
    {
        return hotelName;
    }

    public void setHotelName( String hotelName )
    {
        this.hotelName = hotelName;
    }

    public String getRoomTypeName()
    {
        return roomTypeName;
    }

    public void setRoomTypeName( String roomTypeName )
    {
        this.roomTypeName = roomTypeName;
    }

    public String getRoomNumber()
    {
        return roomNumber;
    }

    public void setRoomNumber( String roomNumber )
    {
        this.roomNumber = roomNumber;
    }

    public LocalDate getCheckInDate()
    {
        return checkInDate;
    }

    public void setCheckInDate( LocalDate checkInDate )
    {
        this.checkInDate = checkInDate;
    }

    public Integer getNumberOfNights()
    {
        return numberOfNights;
    }

    public void setNumberOfNights( Integer numberOfNights )
    {
        this.numberOfNights = numberOfNights;
    }

    public Integer getRoomsRequested()
    {
        return roomsRequested;
    }

    public void setRoomsRequested( Integer roomsRequested )
    {
        this.roomsRequested = roomsRequested;
    }

    public Integer getTotalAdults()
    {
        return totalAdults;
    }

    public void setTotalAdults( Integer totalAdults )
    {
        this.totalAdults = totalAdults;
    }

    public BigDecimal getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice( BigDecimal totalPrice )
    {
        this.totalPrice = totalPrice;
    }

    public String getGuestName()
    {
        return guestName;
    }

    public void setGuestName( String guestName )
    {
        this.guestName = guestName;
    }

    public String getGuestNic()
    {
        return guestNic;
    }

    public void setGuestNic( String guestNic )
    {
        this.guestNic = guestNic;
    }

    public String getGuestPhone()
    {
        return guestPhone;
    }

    public void setGuestPhone( String guestPhone )
    {
        this.guestPhone = guestPhone;
    }

    public String getGuestEmail()
    {
        return guestEmail;
    }

    public void setGuestEmail( String guestEmail )
    {
        this.guestEmail = guestEmail;
    }

    public BookingStatus getStatus()
    {
        return status;
    }

    public void setStatus( BookingStatus status )
    {
        this.status = status;
    }

    public Instant getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( Instant createdAt )
    {
        this.createdAt = createdAt;
    }
}
