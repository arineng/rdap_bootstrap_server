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
package net.arin.rdap_rr;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @version $Rev$, $Date$
 */
public class TldAllocationsTest
{
    @Test
    public void testAllocations() throws Exception
    {
        TldAllocations tldAllocations = new TldAllocations();
        tldAllocations.loadData();

        assertEquals( "://rdap.XN--0ZWM56D", tldAllocations.getUrl( "XN--0ZWM56D" ) );
        assertEquals( "://rdap.COM", tldAllocations.getUrl( "COM" ) );
        assertEquals( "://rdap.AERO", tldAllocations.getUrl( "AERO" ) );
        assertEquals( "://rdap.DE", tldAllocations.getUrl( "DE" ) );
        assertEquals( "://rdap.DE", tldAllocations.getUrl( "de" ) );
        assertEquals( "://rdap.AERO", tldAllocations.getUrl( "aero" ) );
    }
}
