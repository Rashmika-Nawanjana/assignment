package com.suntravels.backend.controller;

import com.suntravels.backend.dto.ChangePasswordRequest;
import com.suntravels.backend.dto.ProfileUpdateRequest;
import com.suntravels.backend.dto.UserResponse;
import com.suntravels.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/profile" )
public class ProfileController
{
    private final UserService userService;

    public ProfileController( UserService userService )
    {
        this.userService = userService;
    }

    @GetMapping
    public UserResponse me( Authentication authentication )
    {
        return userService.getProfile( authentication.getName() );
    }

    @PutMapping
    public UserResponse update( Authentication authentication,
                                @Valid @RequestBody ProfileUpdateRequest request )
    {
        return userService.updateProfile( authentication.getName(), request );
    }

    @PutMapping( "/password" )
    public void changePassword( Authentication authentication,
                                @Valid @RequestBody ChangePasswordRequest request )
    {
        userService.changePassword( authentication.getName(), request );
    }
}
