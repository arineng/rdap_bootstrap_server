/*
 * Copyright (C) 2013 American Registry for Internet Numbers (ARIN)
 */
package net.arin.rdap_rr;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @version $Rev$, $Date$
 */
public class TldAllocationsTest
{
    @Test
    public void testAllocations() throws Exception
    {
        TldAllocations tldAllocations = new TldAllocations();
        tldAllocations.loadData();

        assertEquals( "http://rdap.XN--0ZWM56D", tldAllocations.getUrl( "XN--0ZWM56D" ) );
        assertEquals( "http://rdap.COM", tldAllocations.getUrl( "COM" ) );
        assertEquals( "http://rdap.AERO", tldAllocations.getUrl( "AERO" ) );
        assertEquals( "http://rdap.DE", tldAllocations.getUrl( "DE" ) );
        assertEquals( "http://rdap.DE", tldAllocations.getUrl( "de" ) );
        assertEquals( "http://rdap.AERO", tldAllocations.getUrl( "aero" ) );
    }
}
