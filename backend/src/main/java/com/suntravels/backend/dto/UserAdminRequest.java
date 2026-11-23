package com.suntravels.backend.dto;

import com.suntravels.backend.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserAdminRequest
{
    @NotBlank
    @Size( min = 3, max = 50 )
    private String username;

    /** Required on create; optional on update */
    private String password;

    @NotBlank
    @Size( max = 120 )
    private String fullName;

    @Email
    @Size( max = 120 )
    private String email;

    @NotNull
    private Role role;

    private boolean active = true;

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole( Role role )
    {
        this.role = role;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }
}
