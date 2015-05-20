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

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * @version $Rev$, $Date$
 */
public class IpV4BootstrapTest
{
    @Test
    public void testAllocations() throws Exception
    {
        IpV4Bootstrap v4 = new IpV4Bootstrap();
        v4.loadData( new ResourceFiles() );

        assertNull( v4.getServiceUrls( 3 ) );
        assertEquals( "http://rdap.apnic.net", v4.getServiceUrls( 1 ).getHttpUrl() );
        //TODO renable when their server are put back in the bootstrap files
        //assertEquals( "http://rdap.iana.org", v4.getServiceUrls( 0 ).getHttpUrl() );
        assertEquals( "http://rdap.apnic.net", v4.getServiceUrls( 27 ).getHttpUrl() );
        assertEquals( "http://rdap.db.ripe.net", v4.getServiceUrls( 31 ).getHttpUrl() );
        //TODO renable when their server are put back in the bootstrap files
        //assertEquals( "http://rdap.rd.me.afrinic.net/whois/AFRINIC", v4.getServiceUrls( 41 ).getHttpUrl() );
        assertEquals( "http://rdap.lacnic.net/rdap", v4.getServiceUrls( 177 ).getHttpUrl() );
        assertEquals( "http://rdap.db.ripe.net", v4.getServiceUrls( 188 ).getHttpUrl() );
        assertEquals( "http://rdap.lacnic.net/rdap", v4.getServiceUrls( 191 ).getHttpUrl() );
        assertNull( v4.getServiceUrls( 224 ) );
    }

}
