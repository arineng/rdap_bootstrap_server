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

        assertEquals( "://rdap.arin.net", v4.getUrl( 3 ) );
        assertEquals( "://rdap.apnic.net", v4.getUrl( 1 ) );
        assertEquals( "://rdap.iana.net", v4.getUrl( 0 ) );
        assertEquals( "://rdap.apnic.net", v4.getUrl( 27 ) );
        assertEquals( "://rdap.ripe.net", v4.getUrl( 31 ) );
        assertEquals( "://rdap.afrinic.net", v4.getUrl( 41 ) );
        assertEquals( "://rdap.lacnic.net", v4.getUrl( 177 ) );
        assertEquals( "://rdap.ripe.net", v4.getUrl( 188 ) );
        assertEquals( "://rdap.lacnic.net", v4.getUrl( 191 ) );
        assertEquals( "://rdap.iana.net", v4.getUrl( 224 ) );
    }
}
