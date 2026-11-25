package com.suntravels.backend.service;

import com.suntravels.backend.dto.LoginRequest;
import com.suntravels.backend.dto.LoginResponse;
import com.suntravels.backend.dto.SignupRequest;
import com.suntravels.backend.entity.Role;
import com.suntravels.backend.entity.UserAccount;
import com.suntravels.backend.repository.UserAccountRepository;
import com.suntravels.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Unit tests for login / signup auth rules (easy viva talking point).
 */
@ExtendWith( MockitoExtension.class )
class AuthServiceTest
{
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserAccount agent;

    @BeforeEach
    void setUp()
    {
        agent = new UserAccount();
        agent.setId( 5L );
        agent.setUsername( "agent" );
        agent.setFullName( "Call Center Agent" );
        agent.setEmail( "agent@suntravels.local" );
        agent.setRole( Role.AGENT );
        agent.setActive( true );
    }

    @Test
    void loginReturnsTokenForActiveUser()
    {
        LoginRequest request = loginRequest( "agent", "Agent@12345" );
        when( authenticationManager.authenticate( any( UsernamePasswordAuthenticationToken.class ) ) )
                .thenReturn( new UsernamePasswordAuthenticationToken( "agent", "Agent@12345" ) );
        when( userAccountRepository.findByUsername( "agent" ) ).thenReturn( Optional.of( agent ) );
        when( jwtService.generateToken( "agent", Role.AGENT ) ).thenReturn( "jwt-token" );

        LoginResponse response = authService.login( request );

        assertEquals( "jwt-token", response.getToken() );
        assertEquals( 5L, response.getUserId() );
        assertEquals( "agent", response.getUsername() );
        assertEquals( Role.AGENT, response.getRole() );
    }

    @Test
    void loginRejectsInvalidCredentials()
    {
        LoginRequest request = loginRequest( "agent", "wrong" );
        when( authenticationManager.authenticate( any( UsernamePasswordAuthenticationToken.class ) ) )
                .thenThrow( new BadCredentialsException( "bad" ) );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.login( request )
        );

        assertEquals( UNAUTHORIZED, ex.getStatusCode() );
        assertTrue( ex.getReason().contains( "Invalid username or password" ) );
        verify( jwtService, never() ).generateToken( any(), any() );
    }

    @Test
    void loginRejectsDisabledAccount()
    {
        agent.setActive( false );
        LoginRequest request = loginRequest( "agent", "Agent@12345" );
        when( authenticationManager.authenticate( any( UsernamePasswordAuthenticationToken.class ) ) )
                .thenReturn( new UsernamePasswordAuthenticationToken( "agent", "Agent@12345" ) );
        when( userAccountRepository.findByUsername( "agent" ) ).thenReturn( Optional.of( agent ) );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.login( request )
        );

        assertEquals( UNAUTHORIZED, ex.getStatusCode() );
        assertTrue( ex.getReason().contains( "disabled" ) );
        verify( jwtService, never() ).generateToken( any(), any() );
    }

    @Test
    void signupCreatesActiveAgentWithEncodedPassword()
    {
        SignupRequest request = signupRequest( "newagent", "Agent@12345", "New Agent", "a@b.com" );
        when( userAccountRepository.existsByUsername( "newagent" ) ).thenReturn( false );
        when( passwordEncoder.encode( "Agent@12345" ) ).thenReturn( "encoded-hash" );
        when( userAccountRepository.save( any( UserAccount.class ) ) ).thenAnswer( inv -> {
            UserAccount saved = inv.getArgument( 0 );
            saved.setId( 42L );
            return saved;
        } );
        when( jwtService.generateToken( eq( "newagent" ), eq( Role.AGENT ) ) ).thenReturn( "signup-jwt" );

        LoginResponse response = authService.signup( request );

        assertEquals( "signup-jwt", response.getToken() );
        assertEquals( Role.AGENT, response.getRole() );
        assertEquals( "newagent", response.getUsername() );

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass( UserAccount.class );
        verify( userAccountRepository ).save( captor.capture() );
        UserAccount saved = captor.getValue();
        assertEquals( "newagent", saved.getUsername() );
        assertEquals( "encoded-hash", saved.getPassword() );
        assertEquals( Role.AGENT, saved.getRole() );
        assertTrue( saved.isActive() );
    }

    @Test
    void signupRejectsDuplicateUsername()
    {
        SignupRequest request = signupRequest( "agent", "Agent@12345", "Someone", null );
        when( userAccountRepository.existsByUsername( "agent" ) ).thenReturn( true );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.signup( request )
        );

        assertEquals( BAD_REQUEST, ex.getStatusCode() );
        assertTrue( ex.getReason().contains( "already taken" ) );
        verify( userAccountRepository, never() ).save( any() );
    }

    @Test
    void signupRejectsWeakPassword()
    {
        SignupRequest request = signupRequest( "newbie", "weak", "Newbie", null );
        when( userAccountRepository.existsByUsername( "newbie" ) ).thenReturn( false );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.signup( request )
        );

        assertEquals( BAD_REQUEST, ex.getStatusCode() );
        assertTrue( ex.getReason().toLowerCase().contains( "password" ) );
        verify( userAccountRepository, never() ).save( any() );
        verify( passwordEncoder, never() ).encode( any() );
    }

    private LoginRequest loginRequest( String username, String password )
    {
        LoginRequest request = new LoginRequest();
        request.setUsername( username );
        request.setPassword( password );
        return request;
    }

    private SignupRequest signupRequest( String username, String password, String fullName, String email )
    {
        SignupRequest request = new SignupRequest();
        request.setUsername( username );
        request.setPassword( password );
        request.setFullName( fullName );
        request.setEmail( email );
        return request;
    }
}
