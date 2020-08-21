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
import static net.arin.rdap_bootstrap.service.TestConstants.AFRINIC_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.APNIC_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.ARIN_HTTP;
import static net.arin.rdap_bootstrap.service.TestConstants.ARIN_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.LACNIC_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.RIPE_HTTPS;

public class AsBootstrapTest
{
    @Test
    public void testBootstrap() throws Exception
    {
        AsBootstrap asBootstrap = new AsBootstrap();
        asBootstrap.loadData( new ResourceFiles() );

        assertEquals( AFRINIC_HTTP, asBootstrap.getServiceUrls( "36864" ).getHttpUrl() );
        assertEquals( AFRINIC_HTTPS, asBootstrap.getServiceUrls( "329727" ).getHttpsUrl() );

        assertNull( asBootstrap.getServiceUrls( "4608" ).getHttpUrl() );
        assertEquals( APNIC_HTTPS, asBootstrap.getServiceUrls( "4608" ).getHttpsUrl() );
        assertEquals( APNIC_HTTPS, asBootstrap.getServiceUrls( "140603" ).getHttpsUrl() );

        assertEquals( ARIN_HTTP, asBootstrap.getServiceUrls( "1" ).getHttpUrl() );
        assertEquals( ARIN_HTTPS, asBootstrap.getServiceUrls( "399259" ).getHttpsUrl() );

        assertNull( asBootstrap.getServiceUrls( "27648" ).getHttpUrl() );
        assertEquals( LACNIC_HTTPS, asBootstrap.getServiceUrls( "27648" ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, asBootstrap.getServiceUrls( "271774" ).getHttpsUrl() );

        assertNull( asBootstrap.getServiceUrls( "1877" ).getHttpUrl() );
        assertEquals( RIPE_HTTPS, asBootstrap.getServiceUrls( "1877" ).getHttpsUrl() );
        assertEquals( RIPE_HTTPS, asBootstrap.getServiceUrls( "213403" ).getHttpsUrl() );

        assertNull( asBootstrap.getServiceUrls( "4294967294" ) );
    }
}
