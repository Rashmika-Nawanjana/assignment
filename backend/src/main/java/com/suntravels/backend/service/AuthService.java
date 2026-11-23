package com.suntravels.backend.service;

import com.suntravels.backend.dto.LoginRequest;
import com.suntravels.backend.dto.LoginResponse;
import com.suntravels.backend.dto.SignupRequest;
import com.suntravels.backend.entity.Role;
import com.suntravels.backend.entity.UserAccount;
import com.suntravels.backend.repository.UserAccountRepository;
import com.suntravels.backend.security.JwtService;
import com.suntravels.backend.util.PasswordPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService
{
    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService( AuthenticationManager authenticationManager,
                        UserAccountRepository userAccountRepository,
                        JwtService jwtService,
                        PasswordEncoder passwordEncoder )
    {
        this.authenticationManager = authenticationManager;
        this.userAccountRepository = userAccountRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login( LoginRequest request )
    {
        try
        {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        }
        catch( AuthenticationException ex )
        {
            throw new ResponseStatusException( UNAUTHORIZED, "Invalid username or password" );
        }

        UserAccount user = userAccountRepository.findByUsername( request.getUsername() )
                .orElseThrow( () -> new ResponseStatusException( UNAUTHORIZED, "Invalid username or password" ) );

        if( !user.isActive() )
        {
            throw new ResponseStatusException( UNAUTHORIZED, "Account is disabled" );
        }

        return toLoginResponse( user );
    }

    /**
     * Public signup — always creates an AGENT account.
     */
    @Transactional
    public LoginResponse signup( SignupRequest request )
    {
        String username = request.getUsername().trim();
        if( userAccountRepository.existsByUsername( username ) )
        {
            throw new ResponseStatusException( BAD_REQUEST, "Username is already taken" );
        }

        PasswordPolicy.validate( request.getPassword() );

        UserAccount user = new UserAccount();
        user.setUsername( username );
        user.setPassword( passwordEncoder.encode( request.getPassword() ) );
        user.setFullName( request.getFullName().trim() );
        user.setEmail( request.getEmail() == null ? null : request.getEmail().trim() );
        user.setRole( Role.AGENT );
        user.setActive( true );
        userAccountRepository.save( user );

        return toLoginResponse( user );
    }

    private LoginResponse toLoginResponse( UserAccount user )
    {
        String token = jwtService.generateToken( user.getUsername(), user.getRole() );
        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
