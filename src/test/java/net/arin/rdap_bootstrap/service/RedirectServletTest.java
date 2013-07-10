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

import net.arin.rdap_bootstrap.service.RedirectServlet;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @version $Rev$, $Date$
 */
public class RedirectServletTest
{
    @Test
    public void testMakeAutNumInt() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();

        assertEquals( 10, servlet.makeAutNumLong( "/autnum/10" ) );
        assertEquals( 42222, servlet.makeAutNumLong( "/autnum/42222" ) );
    }

    @Test
    public void testMakeIpBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "://rdap.arin.net", servlet.makeIpBase( "/ip/3.0.0.0" ) );
        assertEquals( "://rdap.arin.net", servlet.makeIpBase( "/ip/3.0.0.0/16" ) );
        assertEquals( "://rdap.lacnic.net", servlet.makeIpBase( "/ip/191.0.1.0" ) );
        assertEquals( "://rdap.lacnic.net", servlet.makeIpBase( "/ip/191.0.1.0/24" ) );
        assertEquals( "://rdap.arin.net", servlet.makeIpBase( "/ip/2620:0000:0000:0000:0000:0000:0000:0000" ) );
        assertEquals( "://rdap.afrinic.net", servlet.makeIpBase( "/ip/2c00:0000::/12" ) );
        assertEquals( "://rdap.lacnic.net", servlet.makeIpBase( "/ip/2800:0000::/12" ) );
        assertEquals( "://rdap.iana.net", servlet.makeIpBase( "/ip/2001:0000::1" ) );
    }

    @Test
    public void testMakeDomainBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "://rdap.XN--0ZWM56D", servlet.makeDomainBase( "/domain/example.XN--0ZWM56D" ) );
        assertEquals( "://rdap.XN--0ZWM56D", servlet.makeDomainBase( "/domain/example.XN--0ZWM56D." ) );
        assertEquals( "://rdap.COM", servlet.makeDomainBase( "/domain/example.COM" ) );
        assertEquals( "://rdap.COM", servlet.makeDomainBase( "/domain/example.COM." ) );
        assertEquals( "://rdap.arin.net", servlet.makeDomainBase( "/domain/0.0.0.3.in-addr.arpa." ) );
        assertEquals( "://rdap.arin.net", servlet.makeDomainBase( "/domain/0.0.0.3.in-addr.arpa" ) );
        assertEquals( "://rdap.arin.net", servlet.makeDomainBase( "/domain/0.3.in-addr.arpa" ) );
        assertEquals( "://rdap.arin.net", servlet.makeDomainBase( "/domain/3.in-addr.arpa" ) );
        assertEquals( "://rdap.arin.net", servlet.makeDomainBase( "/domain/0.2.6.2.ip6.arpa" ) );
        assertEquals( "://rdap.afrinic.net", servlet.makeDomainBase( "/domain/0.c.2.ip6.arpa" ) );
        assertEquals( "://rdap.lacnic.net", servlet.makeDomainBase( "/domain/0.0.8.2.ip6.arpa" ) );
    }

    @Test
    public void testMakeNameserverBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "://rdap.XN--0ZWM56D", servlet.makeNameserverBase( "/nameserver/ns1.example.XN--0ZWM56D" ) );
        assertEquals( "://rdap.XN--0ZWM56D", servlet.makeNameserverBase( "/nameserver/ns1.example.XN--0ZWM56D." ) );
        assertEquals( "://rdap.COM", servlet.makeNameserverBase( "/nameserver/ns1.example.COM" ) );
        assertEquals( "://rdap.COM", servlet.makeNameserverBase( "/nameserver/ns1.example.COM." ) );
    }

    @Test
    public void testMakeEntityBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "://rdap.arin.net", servlet.makeEntityBase( "/entity/ABC123-ARIN" ) );
        assertEquals( "://rdap.ripe.net", servlet.makeEntityBase( "/entity/ABC123-RIPE" ) );
        assertEquals( "://rdap.apnic.net", servlet.makeEntityBase( "/entity/ABC123-AP" ) );
        assertEquals( "://rdap.lacnic.net", servlet.makeEntityBase( "/entity/ABC123-LACNIC" ) );
        assertEquals( "://rdap.afrinic.net", servlet.makeEntityBase( "/entity/ABC123-AFRINIC" ) );
    }
}
