/*
 * Copyright (C) 2013,2015 American Registry for Internet Numbers (ARIN)
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

        assertEquals( "http://rdap.arin.net/registry", asBootstrap.getServiceUrls( "1" ).getHttpUrl() );
        assertEquals( "http://rdap.arin.net/registry", asBootstrap.getServiceUrls( "2" ).getHttpUrl() );
        assertEquals( "https://rdap.db.ripe.net", asBootstrap.getServiceUrls( "7" ).getHttpsUrl() );
        assertEquals( "https://rdap.apnic.net", asBootstrap.getServiceUrls( "173" ).getHttpsUrl() );
        assertEquals( "https://rdap.db.ripe.net", asBootstrap.getServiceUrls( "7" ).getHttpsUrl() );
        assertEquals( "https://rdap.db.ripe.net", asBootstrap.getServiceUrls( "248" ).getHttpsUrl() );
        assertEquals( "https://rdap.db.ripe.net", asBootstrap.getServiceUrls( "251" ).getHttpsUrl() );
        assertEquals( "https://rdap.lacnic.net/rdap", asBootstrap.getServiceUrls( "11450" ).getHttpsUrl() );
        assertEquals( "https://rdap.lacnic.net/rdap", asBootstrap.getServiceUrls( "11451" ).getHttpsUrl() );
        assertEquals( "http://rdap.afrinic.net/rdap", asBootstrap.getServiceUrls( "11569" ).getHttpUrl() );
        assertEquals( "https://rdap.apnic.net", asBootstrap.getServiceUrls( "17408" ).getHttpsUrl() );
        assertEquals( "https://rdap.apnic.net", asBootstrap.getServiceUrls( "18431" ).getHttpsUrl() );
        assertNull( asBootstrap.getServiceUrls( "4294967294" ) );
    }

}
