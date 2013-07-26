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
import net.arin.rdap_bootstrap.service.IpV6Allocations;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @version $Rev$, $Date$
 */
public class IpV6AllocationsTest
{
    @Test
    public void testAllocations() throws Exception
    {
        IpV6Allocations v6 = new IpV6Allocations();
        v6.loadData( new ResourceFiles() );

        assertEquals( "://rdap.arin.net", v6.getUrl( IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:0000" ) ) );
        assertEquals( "://rdap.arin.net", v6.getUrl( IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:ffff" ) ) );
        assertEquals( "://rdap.arin.net", v6.getUrl( IPv6Address.fromString( "2620:01ff:ffff:ffff:ffff:ffff:ffff:0000" ) ) );
        assertEquals( "://rdap.arin.net", v6.getUrl( IPv6Address.fromString( "2620:01ff:ffff:ffff:ffff:ffff:ffff:ffff" ) ) );
        assertEquals( "://rdap.lacnic.net", v6.getUrl( IPv6Address.fromString( "2800:0000:0000:0000:0000:0000:0000:0000" ) ) );
        assertEquals( "://rdap.lacnic.net", v6.getUrl( IPv6Address.fromString( "2800:0000:0000:0000:0000:0000:0000:ffff" ) ) );
        assertEquals( "://rdap.lacnic.net", v6.getUrl( IPv6Address.fromString( "280f:ffff:ffff:ffff:ffff:ffff:ffff:0000" ) ) );
        assertEquals( "://rdap.lacnic.net", v6.getUrl( IPv6Address.fromString( "280f:ffff:ffff:ffff:ffff:ffff:ffff:ffff" ) ) );
        assertEquals( "://rdap.iana.net", v6.getUrl( IPv6Address.fromString( "2001:0000::1" ) ) );
        assertEquals( "://rdap.apnic.net", v6.getUrl( IPv6Network.fromString( "2001:0200::/23" ) ) );
        assertEquals( "://rdap.ripe.net", v6.getUrl( IPv6Address.fromString( "2a00:0000:0000:0000:0000:0000:0000:0000" ) ) );
        assertEquals( "://rdap.ripe.net", v6.getUrl( IPv6Address.fromString( "2a0f:ffff:ffff:ffff:ffff:ffff:ffff:ffff" ) ) );
        assertEquals( "://rdap.afrinic.net", v6.getUrl( IPv6Network.fromString( "2c00:0000::/12" )) );
        assertEquals( "://rdap.lacnic.net", v6.getUrl( IPv6Network.fromString( "2800:0000::/12" ) ) );
    }

    @Test
    public void testIp6HitCounter() throws Exception
    {
        IpV6Allocations v6 = new IpV6Allocations();
        ResourceFiles resourceFiles = new ResourceFiles();
        v6.loadData( resourceFiles );
        Statistics statistics = new Statistics( resourceFiles );
        v6.addIp6CountersToStatistics( statistics );

        v6.getUrl(IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:0000" ), statistics.getIp6RirHitCounter() );
        assertEquals( 1, statistics.getIp6RirHits().get( "ARIN" ).get() );
        assertEquals( 1, statistics.getTotalHits().get() );
        assertEquals( 0, statistics.getTotalMisses().get() );

        v6.getUrl( IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:0000" ), statistics.getIp6RirHitCounter() );
        assertEquals( 2, statistics.getIp6RirHits().get( "ARIN" ).get() );
        assertEquals( 2, statistics.getTotalHits().get() );
        assertEquals( 0, statistics.getTotalMisses().get() );
    }

    @Test
    public void testDomainHitCounter() throws Exception
    {
        IpV6Allocations v6 = new IpV6Allocations();
        ResourceFiles resourceFiles = new ResourceFiles();
        v6.loadData( resourceFiles );
        Statistics statistics = new Statistics( resourceFiles );
        v6.addDomainRirCountersToStatistics( statistics );

        v6.getUrl(IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:0000" ), statistics.getDomainRirHitCounter() );
        assertEquals( 1, statistics.getDomainRirHits().get( "ARIN" ).get() );
        assertEquals( 1, statistics.getTotalHits().get() );
        assertEquals( 0, statistics.getTotalMisses().get() );

        v6.getUrl(IPv6Address.fromString( "2620:0000:0000:0000:0000:0000:0000:0000" ), statistics.getDomainRirHitCounter() );
        assertEquals( 2, statistics.getDomainRirHits().get( "ARIN" ).get() );
        assertEquals( 2, statistics.getTotalHits().get() );
        assertEquals( 0, statistics.getTotalMisses().get() );
    }
}
