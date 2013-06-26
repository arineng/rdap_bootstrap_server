/*
 * Copyright (C) 2013 American Registry for Internet Numbers (ARIN)
 */
package net.arin.rdap_rr;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @version $Rev$, $Date$
 */
public class AsAllocationsTest
{
    @Test
    public void testAllocations() throws Exception
    {
        AsAllocations asAllocations = new AsAllocations();
        asAllocations.loadData();

        assertEquals( "://rdap.arin.net", asAllocations.getUrl( 1 ) );
        assertEquals( "://rdap.iana.net", asAllocations.getUrl( 0 ) );
        assertEquals( "://rdap.arin.net", asAllocations.getUrl( 2 ) );
        assertEquals( "://rdap.ripe.net", asAllocations.getUrl( 7 ) );
        assertEquals( "://rdap.apnic.net", asAllocations.getUrl( 173 ) );
        assertEquals( "://rdap.ripe.net", asAllocations.getUrl( 248 ) );
        assertEquals( "://rdap.ripe.net", asAllocations.getUrl( 251 ) );
        assertEquals( "://rdap.lacnic.net", asAllocations.getUrl( 11450 ) );
        assertEquals( "://rdap.lacnic.net", asAllocations.getUrl( 11451 ) );
        assertEquals( "://rdap.afrinic.net", asAllocations.getUrl( 11569 ) );
        assertEquals( "://rdap.apnic.net", asAllocations.getUrl( 17408 ) );
        assertEquals( "://rdap.apnic.net", asAllocations.getUrl( 18431 ) );
        assertEquals( "://rdap.iana.net", asAllocations.getUrl( 394240 ) );
        assertEquals( "://rdap.iana.net", asAllocations.getUrl( 4294967294L ) );
    }
}
