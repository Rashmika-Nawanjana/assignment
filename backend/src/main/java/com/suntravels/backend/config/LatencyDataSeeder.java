package com.suntravels.backend.config;

import com.suntravels.backend.entity.Booking;
import com.suntravels.backend.entity.BookingStatus;
import com.suntravels.backend.entity.Contract;
import com.suntravels.backend.entity.Hotel;
import com.suntravels.backend.entity.Role;
import com.suntravels.backend.entity.RoomType;
import com.suntravels.backend.entity.UserAccount;
import com.suntravels.backend.repository.BookingRepository;
import com.suntravels.backend.repository.HotelRepository;
import com.suntravels.backend.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds a larger dummy dataset (hotels, contracts, room types, bookings, agents).
 * Runs once; skips if any "Seed Hotel …" row already exists.
 */
@Component
@Order( 2 )
public class LatencyDataSeeder implements CommandLineRunner
{
    private static final Logger log = LoggerFactory.getLogger( LatencyDataSeeder.class );
    private static final String SEED_PREFIX = "Seed Hotel ";
    private static final String[] ROOM_NAMES = { "Standard", "Deluxe", "Suite", "Sea View" };
    private static final String[] CITY_WORDS = {
            "Colombo", "Kandy", "Galle", "Negombo", "Bentota", "Ella", "Nuwara", "Mirissa",
            "Trinco", "Jaffna", "Matara", "Hikkaduwa", "Unawatuna", "Sigiriya", "Dambulla"
    };

    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final int hotelCount;
    private final int bookingCount;
    private final int agentCount;

    public LatencyDataSeeder( HotelRepository hotelRepository,
                              BookingRepository bookingRepository,
                              UserAccountRepository userAccountRepository,
                              PasswordEncoder passwordEncoder,
                              @Value( "${app.seed.latency-data:true}" ) boolean enabled,
                              @Value( "${app.seed.latency-hotels:50}" ) int hotelCount,
                              @Value( "${app.seed.latency-bookings:400}" ) int bookingCount,
                              @Value( "${app.seed.latency-agents:20}" ) int agentCount )
    {
        this.hotelRepository = hotelRepository;
        this.bookingRepository = bookingRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.hotelCount = hotelCount;
        this.bookingCount = bookingCount;
        this.agentCount = agentCount;
    }

    @Override
    @Transactional
    public void run( String... args )
    {
        if( !enabled )
        {
            log.info( "Dummy seed skipped (app.seed.latency-data=false)" );
            return;
        }
        if( hotelRepository.countByNameStartingWith( SEED_PREFIX ) > 0 )
        {
            log.info( "Dummy seed already present — skipping" );
            return;
        }

        log.info( "Seeding dummy data: {} hotels, {} bookings, {} agents…",
                hotelCount, bookingCount, agentCount );

        UserAccount agent = ensurePrimaryAgent();
        List<UserAccount> agents = seedExtraAgents();
        agents.add( 0, agent );

        List<RoomType> allRoomTypes = seedHotelsAndContracts();
        seedBookings( allRoomTypes, agents );

        log.info( "Dummy seed complete ({} room types across {} hotels)",
                allRoomTypes.size(),
                hotelCount );
    }

    private UserAccount ensurePrimaryAgent()
    {
        return userAccountRepository.findByUsername( "agent" )
                .orElseThrow( () -> new IllegalStateException( "Demo agent user missing — run DataInitializer first" ) );
    }

    private List<UserAccount> seedExtraAgents()
    {
        List<UserAccount> agents = new ArrayList<>();
        String encoded = passwordEncoder.encode( "Agent@12345" );
        for( int i = 1; i <= agentCount; i++ )
        {
            String username = "agent" + String.format( "%02d", i );
            UserAccount user = userAccountRepository.findByUsername( username ).orElseGet( UserAccount::new );
            user.setUsername( username );
            user.setPassword( encoded );
            user.setFullName( "Seed Agent " + i );
            user.setEmail( username + "@suntravels.local" );
            user.setRole( Role.AGENT );
            user.setActive( true );
            agents.add( userAccountRepository.save( user ) );
        }
        return agents;
    }

    private List<RoomType> seedHotelsAndContracts()
    {
        LocalDate validFrom = LocalDate.now().minusMonths( 1 );
        LocalDate validTo = LocalDate.now().plusYears( 1 );
        List<RoomType> allRoomTypes = new ArrayList<>();

        for( int i = 1; i <= hotelCount; i++ )
        {
            String city = CITY_WORDS[( i - 1 ) % CITY_WORDS.length];
            Hotel hotel = new Hotel();
            hotel.setName( String.format( "Seed Hotel %03d · %s", i, city ) );

            Contract contract = new Contract();
            contract.setHotel( hotel );
            contract.setValidFrom( validFrom );
            contract.setValidTo( validTo );
            // Mix markups so price sorting/filtering is interesting
            contract.setMarkup( BigDecimal.valueOf( 1.10 + ( i % 6 ) * 0.05 ).setScale( 3, RoundingMode.HALF_UP ) );

            List<RoomType> rooms = new ArrayList<>();
            for( int r = 0; r < ROOM_NAMES.length; r++ )
            {
                RoomType room = new RoomType();
                room.setContract( contract );
                room.setName( ROOM_NAMES[r] );
                room.setPricePerPersonPerNight(
                        BigDecimal.valueOf( 40 + ( i % 12 ) * 15 + r * 25 ).setScale( 2, RoundingMode.HALF_UP )
                );
                room.setNumberOfRooms( 8 + ( i % 10 ) + r * 2 );
                room.setMaxAdults( 2 + ( r % 3 ) );
                rooms.add( room );
            }
            contract.setRoomTypes( rooms );
            hotel.setContracts( List.of( contract ) );

            Hotel saved = hotelRepository.save( hotel );
            allRoomTypes.addAll( saved.getContracts().getFirst().getRoomTypes() );
        }

        return allRoomTypes;
    }

    private void seedBookings( List<RoomType> roomTypes, List<UserAccount> agents )
    {
        if( roomTypes.isEmpty() )
        {
            return;
        }

        List<Booking> batch = new ArrayList<>( bookingCount );
        LocalDate base = LocalDate.now().plusDays( 1 );

        for( int i = 0; i < bookingCount; i++ )
        {
            RoomType roomType = roomTypes.get( i % roomTypes.size() );
            Contract contract = roomType.getContract();
            UserAccount bookedBy = agents.get( i % agents.size() );

            int nights = 1 + ( i % 5 );
            int adults = 1 + ( i % Math.max( 1, roomType.getMaxAdults() ) );
            LocalDate checkIn = base.plusDays( i % 90 );

            BigDecimal total = roomType.getPricePerPersonPerNight()
                    .multiply( contract.getMarkup() )
                    .multiply( BigDecimal.valueOf( nights ) )
                    .multiply( BigDecimal.valueOf( adults ) )
                    .setScale( 2, RoundingMode.HALF_UP );

            Booking booking = new Booking();
            booking.setBookedBy( bookedBy );
            booking.setRoomType( roomType );
            booking.setHotelName( contract.getHotel().getName() );
            booking.setRoomTypeName( roomType.getName() );
            booking.setRoomNumber( String.valueOf( 1 + ( i % Math.max( 1, roomType.getNumberOfRooms() ) ) ) );
            booking.setCheckInDate( checkIn );
            booking.setNumberOfNights( nights );
            booking.setRoomsRequested( 1 );
            booking.setTotalAdults( adults );
            booking.setTotalPrice( total );
            booking.setGuestName( "Guest " + String.format( "%04d", i + 1 ) );
            booking.setGuestNic( "NIC" + String.format( "%08d", 10000000 + i ) );
            booking.setGuestPhone( "+9477" + String.format( "%07d", 1000000 + i ) );
            booking.setGuestEmail( "guest" + ( i + 1 ) + "@example.com" );
            // Mostly confirmed; some cancelled so status filters matter
            booking.setStatus( i % 9 == 0 ? BookingStatus.CANCELLED : BookingStatus.CONFIRMED );
            booking.setCreatedAt( Instant.now().minusSeconds( i * 3600L ) );
            batch.add( booking );
        }

        bookingRepository.saveAll( batch );
    }
}
