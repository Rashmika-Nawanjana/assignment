package com.suntravels.backend.controller;

import com.suntravels.backend.dto.PageResponse;
import com.suntravels.backend.dto.UserAdminRequest;
import com.suntravels.backend.dto.UserResponse;
import com.suntravels.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/users" )
public class UserController
{
    private final UserService userService;

    public UserController( UserService userService )
    {
        this.userService = userService;
    }

    @GetMapping
    public PageResponse<UserResponse> list( @RequestParam( defaultValue = "" ) String q,
                                            @RequestParam( defaultValue = "0" ) int page,
                                            @RequestParam( defaultValue = "8" ) int size )
    {
        return userService.listUsers( q, page, size );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    public UserResponse create( @Valid @RequestBody UserAdminRequest request )
    {
        return userService.createUser( request );
    }

    @PutMapping( "/{id}" )
    public UserResponse update( @PathVariable Long id,
                                @Valid @RequestBody UserAdminRequest request )
    {
        return userService.updateUser( id, request );
    }

    @DeleteMapping( "/{id}" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void delete( @PathVariable Long id )
    {
        userService.deleteUser( id );
    }
}
