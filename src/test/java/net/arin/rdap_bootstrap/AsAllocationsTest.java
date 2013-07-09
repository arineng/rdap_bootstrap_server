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
package net.arin.rdap_bootstrap;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @version $Rev$, $Date$
 */
public class AsAllocationsTest
{
    @Test
    public void testAllocations() throws Exception
    {
        AsAllocations asAllocations = new AsAllocations();
        asAllocations.loadData();

        assertEquals( "://rdap.arin.net", asAllocations.getUrl( 1 ) );
        assertEquals( "://rdap.iana.net", asAllocations.getUrl( 0 ) );
        assertEquals( "://rdap.arin.net", asAllocations.getUrl( 2 ) );
        assertEquals( "://rdap.ripe.net", asAllocations.getUrl( 7 ) );
        assertEquals( "://rdap.apnic.net", asAllocations.getUrl( 173 ) );
        assertEquals( "://rdap.ripe.net", asAllocations.getUrl( 248 ) );
        assertEquals( "://rdap.ripe.net", asAllocations.getUrl( 251 ) );
        assertEquals( "://rdap.lacnic.net", asAllocations.getUrl( 11450 ) );
        assertEquals( "://rdap.lacnic.net", asAllocations.getUrl( 11451 ) );
        assertEquals( "://rdap.afrinic.net", asAllocations.getUrl( 11569 ) );
        assertEquals( "://rdap.apnic.net", asAllocations.getUrl( 17408 ) );
        assertEquals( "://rdap.apnic.net", asAllocations.getUrl( 18431 ) );
        assertEquals( "://rdap.iana.net", asAllocations.getUrl( 394240 ) );
        assertEquals( "://rdap.iana.net", asAllocations.getUrl( 4294967294L ) );
    }
}
