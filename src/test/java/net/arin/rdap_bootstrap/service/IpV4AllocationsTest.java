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

import net.arin.rdap_bootstrap.service.IpV4Allocations;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @version $Rev$, $Date$
 */
public class IpV4AllocationsTest
{
    @Test
    public void testAllocations() throws Exception
    {
        IpV4Allocations v4 = new IpV4Allocations();
        v4.loadData( new ResourceFiles() );

        assertEquals( "://rdap.arin.net", v4.getUrl( 3 ) );
        assertEquals( "://rdap.apnic.net", v4.getUrl( 1 ) );
        assertEquals( "://rdap.iana.net", v4.getUrl( 0 ) );
        assertEquals( "://rdap.apnic.net", v4.getUrl( 27 ) );
        assertEquals( "://rdap.ripe.net", v4.getUrl( 31 ) );
        assertEquals( "://rdap.afrinic.net", v4.getUrl( 41 ) );
        assertEquals( "://rdap.lacnic.net", v4.getUrl( 177 ) );
        assertEquals( "://rdap.ripe.net", v4.getUrl( 188 ) );
        assertEquals( "://rdap.lacnic.net", v4.getUrl( 191 ) );
        assertEquals( "://rdap.iana.net", v4.getUrl( 224 ) );
    }

    @Test
    public void testIp4HitCounter() throws Exception
    {
        IpV4Allocations v4 = new IpV4Allocations();
        ResourceFiles resourceFiles = new ResourceFiles();
        v4.loadData( resourceFiles );
        Statistics statistics = new Statistics( resourceFiles );
        v4.addIp4CountersToStatistics( statistics );

        v4.getUrl( 3, statistics.getIp4RirHitCounter() );
        assertEquals( 1, statistics.getIp4RirHits().get( "ARIN" ).get() );
        assertEquals( 1, statistics.getTotalHits().get() );
        assertEquals( 0, statistics.getTotalMisses().get() );

        v4.getUrl( 3, statistics.getIp4RirHitCounter() );
        assertEquals( 2, statistics.getIp4RirHits().get( "ARIN" ).get() );
        assertEquals( 2, statistics.getTotalHits().get() );
        assertEquals( 0, statistics.getTotalMisses().get() );
    }

    @Test
    public void testDomainHitCounter() throws Exception
    {
        IpV4Allocations v4 = new IpV4Allocations();
        ResourceFiles resourceFiles = new ResourceFiles();
        v4.loadData( resourceFiles );
        Statistics statistics = new Statistics( resourceFiles );
        v4.addDomainRirCountersToStatistics( statistics );

        v4.getUrl( 3, statistics.getDomainRirHitCounter() );
        assertEquals( 1, statistics.getDomainRirHits().get( "ARIN" ).get() );
        assertEquals( 1, statistics.getTotalHits().get() );
        assertEquals( 0, statistics.getTotalMisses().get() );

        v4.getUrl( 3, statistics.getDomainRirHitCounter() );
        assertEquals( 2, statistics.getDomainRirHits().get( "ARIN" ).get() );
        assertEquals( 2, statistics.getTotalHits().get() );
        assertEquals( 0, statistics.getTotalMisses().get() );
    }
}
