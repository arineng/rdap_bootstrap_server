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
        AsAllocations v4 = new AsAllocations();
        v4.loadData();

        assertEquals( "http://rdap.arin.net", v4.getUrl( 1 ) );
        assertEquals( "http://rdap.iana.net", v4.getUrl( 0 ) );
        assertEquals( "http://rdap.arin.net", v4.getUrl( 2 ) );
        assertEquals( "http://rdap.ripe.net", v4.getUrl( 7 ) );
        assertEquals( "http://rdap.apnic.net", v4.getUrl( 173 ) );
        assertEquals( "http://rdap.ripe.net", v4.getUrl( 248 ) );
        assertEquals( "http://rdap.ripe.net", v4.getUrl( 251 ) );
        assertEquals( "http://rdap.lacnic.net", v4.getUrl( 11450 ) );
        assertEquals( "http://rdap.lacnic.net", v4.getUrl( 11451 ) );
        assertEquals( "http://rdap.afrinic.net", v4.getUrl( 11569 ) );
        assertEquals( "http://rdap.apnic.net", v4.getUrl( 17408 ) );
        assertEquals( "http://rdap.apnic.net", v4.getUrl( 18431 ) );
        assertEquals( "http://rdap.iana.net", v4.getUrl( 394240 ) );
        assertEquals( "http://rdap.iana.net", v4.getUrl( 4294967294L ) );
    }
}
