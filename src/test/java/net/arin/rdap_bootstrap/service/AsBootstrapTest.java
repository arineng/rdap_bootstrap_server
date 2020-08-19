/*
 * Copyright (C) 2013-2020 American Registry for Internet Numbers (ARIN)
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
import static net.arin.rdap_bootstrap.service.TestConstants.AFRINIC_HTTP;
import static net.arin.rdap_bootstrap.service.TestConstants.APNIC_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.ARIN_HTTP;
import static net.arin.rdap_bootstrap.service.TestConstants.LACNIC_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.RIPE_HTTPS;

public class AsBootstrapTest
{
    @Test
    public void testBootstrap() throws Exception
    {
        AsBootstrap asBootstrap = new AsBootstrap();
        asBootstrap.loadData( new ResourceFiles() );

        assertEquals( ARIN_HTTP, asBootstrap.getServiceUrls( "1" ).getHttpUrl() );
        assertEquals( ARIN_HTTP, asBootstrap.getServiceUrls( "2" ).getHttpUrl() );
        assertEquals( RIPE_HTTPS, asBootstrap.getServiceUrls( "7" ).getHttpsUrl() );
        assertEquals( APNIC_HTTPS, asBootstrap.getServiceUrls( "173" ).getHttpsUrl() );
        assertEquals( RIPE_HTTPS, asBootstrap.getServiceUrls( "7" ).getHttpsUrl() );
        assertEquals( RIPE_HTTPS, asBootstrap.getServiceUrls( "248" ).getHttpsUrl() );
        assertEquals( RIPE_HTTPS, asBootstrap.getServiceUrls( "251" ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, asBootstrap.getServiceUrls( "11450" ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, asBootstrap.getServiceUrls( "11451" ).getHttpsUrl() );
        assertEquals( AFRINIC_HTTP, asBootstrap.getServiceUrls( "11569" ).getHttpUrl() );
        assertEquals( APNIC_HTTPS, asBootstrap.getServiceUrls( "17408" ).getHttpsUrl() );
        assertEquals( APNIC_HTTPS, asBootstrap.getServiceUrls( "18431" ).getHttpsUrl() );
        assertNull( asBootstrap.getServiceUrls( "4294967294" ) );
    }
}
