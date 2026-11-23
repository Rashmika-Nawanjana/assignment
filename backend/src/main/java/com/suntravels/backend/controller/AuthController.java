package com.suntravels.backend.controller;

import com.suntravels.backend.dto.LoginRequest;
import com.suntravels.backend.dto.LoginResponse;
import com.suntravels.backend.dto.SignupRequest;
import com.suntravels.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/auth" )
public class AuthController
{
    private final AuthService authService;

    public AuthController( AuthService authService )
    {
        this.authService = authService;
    }

    @PostMapping( "/login" )
    public LoginResponse login( @Valid @RequestBody LoginRequest request )
    {
        return authService.login( request );
    }

    @PostMapping( "/signup" )
    @ResponseStatus( HttpStatus.CREATED )
    public LoginResponse signup( @Valid @RequestBody SignupRequest request )
    {
        return authService.signup( request );
    }
}
