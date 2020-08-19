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

import static junit.framework.Assert.assertEquals;
import static net.arin.rdap_bootstrap.service.TestConstants.AFRINIC_HTTP;
import static net.arin.rdap_bootstrap.service.TestConstants.APNIC_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.ARIN_HTTP;
import static net.arin.rdap_bootstrap.service.TestConstants.LACNIC_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.RIPE_HTTPS;

import org.junit.Test;

public class IpV4BootstrapTest
{
    @Test
    public void testAllocations() throws Exception
    {
        IpV4Bootstrap v4 = new IpV4Bootstrap();
        v4.loadData( new ResourceFiles() );

        // Test prefixes.
        assertEquals( APNIC_HTTPS, v4.getServiceUrls( "1" ).getHttpsUrl() );
        assertEquals( APNIC_HTTPS, v4.getServiceUrls( "27" ).getHttpsUrl() );
        assertEquals( RIPE_HTTPS, v4.getServiceUrls( "31" ).getHttpsUrl() );
        assertEquals( AFRINIC_HTTP, v4.getServiceUrls( "41" ).getHttpUrl() );
        assertEquals( LACNIC_HTTPS, v4.getServiceUrls( "177" ).getHttpsUrl() );
        assertEquals( RIPE_HTTPS, v4.getServiceUrls( "188" ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, v4.getServiceUrls( "191" ).getHttpsUrl() );

        // Test full prefixes.
        assertEquals( ARIN_HTTP, v4.getServiceUrls( "216.0.0.0/8" ).getHttpUrl() );
        assertEquals( LACNIC_HTTPS, v4.getServiceUrls( "177.0.0.0/8" ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, v4.getServiceUrls( "177.0.0.1/32" ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, v4.getServiceUrls( "177.0.0.1" ).getHttpsUrl() );
    }
}
