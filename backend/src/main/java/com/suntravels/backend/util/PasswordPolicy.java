package com.suntravels.backend.util;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Strong password rules for signup / password change.
 * Min 8 chars, upper, lower, digit, special character.
 */
public final class PasswordPolicy
{
    private static final String PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_\\-+={}\\[\\]|:;\"'<>,./~`]).{8,}$";

    private PasswordPolicy()
    {
    }

    public static void validate( String password )
    {
        if( password == null || !password.matches( PATTERN ) )
        {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Password must be at least 8 characters and include uppercase, lowercase, number, and special character."
            );
        }
    }
}
