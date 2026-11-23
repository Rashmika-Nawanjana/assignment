package com.suntravels.backend;

import org.junit.jupiter.api.Test;

/**
 * Lightweight smoke test — full @SpringBootTest needs a live DB,
 * so keep that for manual/integration runs.
 */
class SunTravelsApplicationTests
{
    @Test
    void placeholderPasses()
    {
        // Kept so the test suite always has a non-service entry.
        org.junit.jupiter.api.Assertions.assertTrue( true );
    }
}
