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

import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class IpV6BootstrapTest
{
    private static final String ARIN_HTTP = "http://rdap.arin.net/registry";
    private static final String LACNIC_HTTPS = "https://rdap.lacnic.net/rdap";
    private static final String APNIC_HTTPS = "https://rdap.apnic.net";
    private static final String RIPE_HTTPS = "https://rdap.db.ripe.net";
    private static final String AFRINIC_HTTP = "http://rdap.afrinic.net/rdap";

    @Test
    public void testAllocations() throws Exception
    {
        IpV6Bootstrap v6 = new IpV6Bootstrap();
        v6.loadData( new ResourceFiles() );

        assertEquals( ARIN_HTTP, v6.getServiceUrls( IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:0000" ) ).getHttpUrl() );
        assertEquals( ARIN_HTTP, v6.getServiceUrls( IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:ffff" ) ).getHttpUrl() );
        assertEquals( ARIN_HTTP, v6.getServiceUrls( IPv6Address.fromString( "2620:01ff:ffff:ffff:ffff:ffff:ffff:0000" ) ).getHttpUrl() );
        assertEquals( ARIN_HTTP, v6.getServiceUrls( IPv6Address.fromString( "2620:01ff:ffff:ffff:ffff:ffff:ffff:ffff" ) ).getHttpUrl() );
        assertEquals( LACNIC_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "2800:0000:0000:0000:0000:0000:0000:0000" ) ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "2800:0000:0000:0000:0000:0000:0000:ffff" ) ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "280f:ffff:ffff:ffff:ffff:ffff:ffff:0000" ) ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "280f:ffff:ffff:ffff:ffff:ffff:ffff:ffff" ) ).getHttpsUrl() );
        assertEquals( APNIC_HTTPS, v6.getServiceUrls( IPv6Network.fromString( "2001:0200::/23" ) ).getHttpsUrl() );
        assertEquals( RIPE_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "2a00:0000:0000:0000:0000:0000:0000:0000" ) ).getHttpsUrl() );
        assertEquals( RIPE_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "2a0f:ffff:ffff:ffff:ffff:ffff:ffff:ffff" ) ).getHttpsUrl() );
        assertEquals( AFRINIC_HTTP, v6.getServiceUrls( IPv6Network.fromString( "2c00:0000::/12" ) ).getHttpUrl() );
        assertEquals( LACNIC_HTTPS, v6.getServiceUrls( IPv6Network.fromString( "2800:0000::/12" ) ).getHttpsUrl() );
    }
}
