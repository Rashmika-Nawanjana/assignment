package com.suntravels.backend.config;

import com.suntravels.backend.entity.Role;
import com.suntravels.backend.entity.UserAccount;
import com.suntravels.backend.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds / refreshes demo users with strong passwords.
 */
@Component
@Order( 1 )
public class DataInitializer implements CommandLineRunner
{
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer( UserAccountRepository userAccountRepository,
                            PasswordEncoder passwordEncoder )
    {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run( String... args )
    {
        upsert( "admin", "Admin@12345", "System Admin", "admin@suntravels.local", Role.ADMIN );
        upsert( "manager", "Manager@12345", "Contract Manager", "manager@suntravels.local", Role.MANAGER );
        upsert( "agent", "Agent@12345", "Call Center Agent", "agent@suntravels.local", Role.AGENT );
    }

    private void upsert( String username,
                         String rawPassword,
                         String fullName,
                         String email,
                         Role role )
    {
        UserAccount user = userAccountRepository.findByUsername( username ).orElseGet( UserAccount::new );
        user.setUsername( username );
        user.setPassword( passwordEncoder.encode( rawPassword ) );
        user.setFullName( fullName );
        user.setEmail( email );
        user.setRole( role );
        user.setActive( true );
        userAccountRepository.save( user );
    }
}
