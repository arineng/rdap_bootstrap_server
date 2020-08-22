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
import static net.arin.rdap_bootstrap.service.TestConstants.AFRINIC_HTTP;
import static net.arin.rdap_bootstrap.service.TestConstants.APNIC_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.ARIN_HTTP;
import static net.arin.rdap_bootstrap.service.TestConstants.LACNIC_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.RIPE_HTTPS;
import static org.junit.Assert.assertNull;

public class IpV6BootstrapTest
{
    @Test
    public void testAllocations() throws Exception
    {
        IpV6Bootstrap v6 = new IpV6Bootstrap();
        v6.loadData( new ResourceFiles() );

        assertEquals( AFRINIC_HTTP, v6.getServiceUrls( IPv6Network.fromString( "2c00:0000::/12" ) ).getHttpUrl() );
        assertEquals( AFRINIC_HTTP, v6.getServiceUrls( IPv6Network.fromString( "2c00:0000::/13" ) ).getHttpUrl() );
        assertNull( v6.getServiceUrls( IPv6Network.fromString( "3c00:0000::/12" ) ) );

        assertEquals( APNIC_HTTPS, v6.getServiceUrls( IPv6Network.fromString( "2001:0200::/23" ) ).getHttpsUrl() );

        assertEquals( ARIN_HTTP, v6.getServiceUrls( IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:0000" ) ).getHttpUrl() );
        assertEquals( ARIN_HTTP, v6.getServiceUrls( IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:ffff" ) ).getHttpUrl() );
        assertEquals( ARIN_HTTP, v6.getServiceUrls( IPv6Address.fromString( "2620:01ff:ffff:ffff:ffff:ffff:ffff:0000" ) ).getHttpUrl() );
        assertEquals( ARIN_HTTP, v6.getServiceUrls( IPv6Address.fromString( "2620:01ff:ffff:ffff:ffff:ffff:ffff:ffff" ) ).getHttpUrl() );

        assertEquals( LACNIC_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "2800:0000:0000:0000:0000:0000:0000:0000" ) ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "2800:0000:0000:0000:0000:0000:0000:ffff" ) ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "280f:ffff:ffff:ffff:ffff:ffff:ffff:0000" ) ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "280f:ffff:ffff:ffff:ffff:ffff:ffff:ffff" ) ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, v6.getServiceUrls( IPv6Network.fromString( "2800:0000::/12" ) ).getHttpsUrl() );

        assertEquals( RIPE_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "2a00:0000:0000:0000:0000:0000:0000:0000" ) ).getHttpsUrl() );
        assertEquals( RIPE_HTTPS, v6.getServiceUrls( IPv6Address.fromString( "2a0f:ffff:ffff:ffff:ffff:ffff:ffff:ffff" ) ).getHttpsUrl() );
    }
}
