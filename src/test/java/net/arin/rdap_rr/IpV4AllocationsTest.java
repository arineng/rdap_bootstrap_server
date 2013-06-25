/*
 * Copyright (C) 2013 American Registry for Internet Numbers (ARIN)
 */
package net.arin.rdap_rr;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @version $Rev$, $Date$
 */
public class IpV4AllocationsTest
{
    @Test
    public void testAllocations() throws Exception
    {
        IpV4Allocations v4 = new IpV4Allocations();
        v4.loadData();

        assertEquals( "http://rdap.arin.net", v4.getUrl( 3 ) );
        assertEquals( "http://rdap.apnic.net", v4.getUrl( 1 ) );
        assertEquals( "http://rdap.iana.net", v4.getUrl( 0 ) );
        assertEquals( "http://rdap.apnic.net", v4.getUrl( 27 ) );
        assertEquals( "http://rdap.ripe.net", v4.getUrl( 31 ) );
        assertEquals( "http://rdap.afrinic.net", v4.getUrl( 41 ) );
        assertEquals( "http://rdap.lacnic.net", v4.getUrl( 177 ) );
        assertEquals( "http://rdap.ripe.net", v4.getUrl( 188 ) );
        assertEquals( "http://rdap.lacnic.net", v4.getUrl( 191 ) );
        assertEquals( "http://rdap.iana.net", v4.getUrl( 224 ) );
    }
}
