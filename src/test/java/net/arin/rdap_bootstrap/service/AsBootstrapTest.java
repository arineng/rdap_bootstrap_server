/*
 * Copyright (C) 2013 American Registry for Internet Numbers (ARIN)
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */
package net.arin.rdap_bootstrap.service;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * @version $Rev$, $Date$
 */
public class AsBootstrapTest
{
    @Test
    public void testBootstrap() throws Exception
    {
        AsBootstrap asBootstrap = new AsBootstrap();
        asBootstrap.loadData( new ResourceFiles() );

        assertEquals( "http://rdappilot.arin.net/restfulwhois/rdap", asBootstrap.getServiceUrls( "1" ).getHttpUrl() );
        assertEquals( "http://rdappilot.arin.net/restfulwhois/rdap", asBootstrap.getServiceUrls( "2" ).getHttpUrl() );
        assertEquals( "http://rdap.db.ripe.net", asBootstrap.getServiceUrls( "7" ).getHttpUrl() );
        assertEquals( "http://rdap.apnic.net", asBootstrap.getServiceUrls( "173" ).getHttpUrl() );
        assertEquals( "http://rdap.db.ripe.net", asBootstrap.getServiceUrls( "248" ).getHttpUrl() );
        assertEquals( "http://rdap.db.ripe.net", asBootstrap.getServiceUrls( "251" ).getHttpUrl() );
        assertEquals( "http://rdap.labs.lacnic.net/rdap", asBootstrap.getServiceUrls( "11450" ).getHttpUrl() );
        assertEquals( "http://rdap.labs.lacnic.net/rdap", asBootstrap.getServiceUrls( "11451" ).getHttpUrl() );
        assertEquals( "http://rdap.rd.me.afrinic.net/whois/AFRINIC", asBootstrap.getServiceUrls( "11569" ).getHttpUrl() );
        assertEquals( "http://rdap.apnic.net", asBootstrap.getServiceUrls( "17408" ).getHttpUrl() );
        assertEquals( "http://rdap.apnic.net", asBootstrap.getServiceUrls( "18431" ).getHttpUrl() );
        assertNull( asBootstrap.getServiceUrls( "394240" ) );
        assertNull( asBootstrap.getServiceUrls( "4294967294" ) );
    }

}
