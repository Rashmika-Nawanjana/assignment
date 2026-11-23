package com.suntravels.backend.service;

import com.suntravels.backend.dto.ChangePasswordRequest;
import com.suntravels.backend.dto.PageResponse;
import com.suntravels.backend.dto.ProfileUpdateRequest;
import com.suntravels.backend.dto.UserAdminRequest;
import com.suntravels.backend.dto.UserResponse;
import com.suntravels.backend.entity.UserAccount;
import com.suntravels.backend.repository.UserAccountRepository;
import com.suntravels.backend.util.PasswordPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService
{
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService( UserAccountRepository userAccountRepository,
                        PasswordEncoder passwordEncoder )
    {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional( readOnly = true )
    public PageResponse<UserResponse> listUsers( String query, int page, int size )
    {
        String q = query == null ? "" : query.trim();
        PageRequest pageable = PageRequest.of( page, size, Sort.by( "username" ).ascending() );
        Page<UserAccount> result = q.isEmpty()
                ? userAccountRepository.findAll( pageable )
                : userAccountRepository.search( q, pageable );
        return new PageResponse<>( result.map( this::toResponse ) );
    }

    @Transactional
    public UserResponse createUser( UserAdminRequest request )
    {
        if( userAccountRepository.existsByUsername( request.getUsername().trim() ) )
        {
            throw new ResponseStatusException( BAD_REQUEST, "Username is already taken" );
        }
        if( request.getPassword() == null || request.getPassword().isBlank() )
        {
            throw new ResponseStatusException( BAD_REQUEST, "Password is required for new users" );
        }
        PasswordPolicy.validate( request.getPassword() );

        UserAccount user = new UserAccount();
        applyAdminFields( user, request, true );
        return toResponse( userAccountRepository.save( user ) );
    }

    @Transactional
    public UserResponse updateUser( Long id, UserAdminRequest request )
    {
        UserAccount user = userAccountRepository.findById( id )
                .orElseThrow( () -> new ResponseStatusException( NOT_FOUND, "User not found" ) );

        String newUsername = request.getUsername().trim();
        if( !user.getUsername().equalsIgnoreCase( newUsername )
                && userAccountRepository.existsByUsername( newUsername ) )
        {
            throw new ResponseStatusException( BAD_REQUEST, "Username is already taken" );
        }

        applyAdminFields( user, request, false );
        return toResponse( userAccountRepository.save( user ) );
    }

    @Transactional
    public void deleteUser( Long id )
    {
        if( !userAccountRepository.existsById( id ) )
        {
            throw new ResponseStatusException( NOT_FOUND, "User not found" );
        }
        userAccountRepository.deleteById( id );
    }

    @Transactional( readOnly = true )
    public UserResponse getProfile( String username )
    {
        return toResponse( requireUser( username ) );
    }

    @Transactional
    public UserResponse updateProfile( String username, ProfileUpdateRequest request )
    {
        UserAccount user = requireUser( username );
        user.setFullName( request.getFullName().trim() );
        user.setEmail( request.getEmail() == null ? null : request.getEmail().trim() );
        return toResponse( userAccountRepository.save( user ) );
    }

    @Transactional
    public void changePassword( String username, ChangePasswordRequest request )
    {
        UserAccount user = requireUser( username );
        if( !passwordEncoder.matches( request.getCurrentPassword(), user.getPassword() ) )
        {
            throw new ResponseStatusException( BAD_REQUEST, "Current password is incorrect" );
        }
        PasswordPolicy.validate( request.getNewPassword() );
        user.setPassword( passwordEncoder.encode( request.getNewPassword() ) );
        userAccountRepository.save( user );
    }

    private void applyAdminFields( UserAccount user, UserAdminRequest request, boolean creating )
    {
        user.setUsername( request.getUsername().trim() );
        user.setFullName( request.getFullName().trim() );
        user.setEmail( request.getEmail() == null ? null : request.getEmail().trim() );
        user.setRole( request.getRole() );
        user.setActive( request.isActive() );

        if( creating || ( request.getPassword() != null && !request.getPassword().isBlank() ) )
        {
            PasswordPolicy.validate( request.getPassword() );
            user.setPassword( passwordEncoder.encode( request.getPassword() ) );
        }
    }

    private UserAccount requireUser( String username )
    {
        return userAccountRepository.findByUsername( username )
                .orElseThrow( () -> new ResponseStatusException( NOT_FOUND, "User not found" ) );
    }

    private UserResponse toResponse( UserAccount user )
    {
        UserResponse response = new UserResponse();
        response.setId( user.getId() );
        response.setUsername( user.getUsername() );
        response.setFullName( user.getFullName() );
        response.setEmail( user.getEmail() );
        response.setRole( user.getRole() );
        response.setActive( user.isActive() );
        return response;
    }
}
