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

import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @version $Rev$, $Date$
 */
public class IpV6BootstrapTest
{

    private static final String ARIN = "http://rdappilot.arin.net/restfulwhois/rdap";
    private static final String LACNIC = "http://rdap.labs.lacnic.net/rdap";
    private static final String IANA = "http://rdap.iana.org";
    private static final String APNIC = "http://rdap.apnic.net";
    private static final String RIPE = "http://rdap.db.ripe.net";
    private static final String AFRINIC = "http://rdap.rd.me.afrinic.net/whois/AFRINIC";

    @Test
    public void testAllocations() throws Exception
    {
        IpV6Bootstrap v6 = new IpV6Bootstrap();
        v6.loadData( new ResourceFiles() );

        assertEquals( ARIN, v6.getServiceUrls( IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:0000" ) ).getHttpUrl() );
        assertEquals( ARIN, v6.getServiceUrls( IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:ffff" ) ).getHttpUrl() );
        assertEquals( ARIN, v6.getServiceUrls( IPv6Address.fromString( "2620:01ff:ffff:ffff:ffff:ffff:ffff:0000" ) ).getHttpUrl() );
        assertEquals( ARIN, v6.getServiceUrls( IPv6Address.fromString( "2620:01ff:ffff:ffff:ffff:ffff:ffff:ffff" ) ).getHttpUrl() );
        assertEquals( LACNIC, v6.getServiceUrls( IPv6Address.fromString( "2800:0000:0000:0000:0000:0000:0000:0000" ) ).getHttpUrl() );
        assertEquals( LACNIC, v6.getServiceUrls( IPv6Address.fromString( "2800:0000:0000:0000:0000:0000:0000:ffff" ) ).getHttpUrl() );
        assertEquals( LACNIC, v6.getServiceUrls( IPv6Address.fromString( "280f:ffff:ffff:ffff:ffff:ffff:ffff:0000" ) ).getHttpUrl() );
        assertEquals( LACNIC, v6.getServiceUrls( IPv6Address.fromString( "280f:ffff:ffff:ffff:ffff:ffff:ffff:ffff" ) ).getHttpUrl() );
        assertEquals( IANA, v6.getServiceUrls( IPv6Address.fromString( "2001:0000::1" ) ).getHttpUrl() );
        assertEquals( APNIC, v6.getServiceUrls( IPv6Network.fromString( "2001:0200::/23" ) ).getHttpUrl() );
        assertEquals( RIPE, v6.getServiceUrls( IPv6Address.fromString( "2a00:0000:0000:0000:0000:0000:0000:0000" ) ).getHttpUrl() );
        assertEquals( RIPE, v6.getServiceUrls( IPv6Address.fromString( "2a0f:ffff:ffff:ffff:ffff:ffff:ffff:ffff" ) ).getHttpUrl() );
        assertEquals( AFRINIC, v6.getServiceUrls( IPv6Network.fromString( "2c00:0000::/12" ) ).getHttpUrl() );
        assertEquals( LACNIC, v6.getServiceUrls( IPv6Network.fromString( "2800:0000::/12" ) ).getHttpUrl() );
    }
}
