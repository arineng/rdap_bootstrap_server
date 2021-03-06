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
import static net.arin.rdap_bootstrap.service.TestConstants.GOOGLE_HTTPS;
import static net.arin.rdap_bootstrap.service.TestConstants.INFO_HTTPS;

public class DomainBootstrapTest
{
    @Test
    public void testAllocations() throws Exception
    {
        DomainBootstrap domain = new DomainBootstrap();
        domain.loadData( new ResourceFiles() );

        assertEquals( GOOGLE_HTTPS, domain.getServiceUrls( "xn--flw351e" ).getHttpsUrl() );

        assertEquals( INFO_HTTPS, domain.getServiceUrls( "foo.info" ).getHttpsUrl() );
        assertEquals( INFO_HTTPS, domain.getServiceUrls( "info" ).getHttpsUrl() );
    }
}
