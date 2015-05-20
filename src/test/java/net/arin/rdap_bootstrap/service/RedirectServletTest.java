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
    private static final String ARIN = "http://rdappilot.arin.net/restfulwhois/rdap";
    private static final String LACNIC = "http://rdap.lacnic.net/rdap";
    private static final String IANA = "http://rdap.iana.org";
    private static final String APNIC = "http://rdap.apnic.net";
    private static final String RIPE = "http://rdap.db.ripe.net";
    private static final String AFRINIC = "http://rdap.rd.me.afrinic.net/whois/AFRINIC";
    private static final String COM = "http://tlab.verisign.com/COM";

    @Test
    public void testMakeAutNumInt() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( ARIN, servlet.makeAutnumBase( "/autnum/10" ).getHttpUrl() );
        assertEquals( RIPE, servlet.makeAutnumBase( "/autnum/42222" ).getHttpUrl() );
    }

    @Test
    public void testMakeIpBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( ARIN, servlet.makeIpBase( "/ip/7.0.0.0" ).getHttpUrl() );
        assertEquals( ARIN, servlet.makeIpBase( "/ip/7.0.0.0/16" ).getHttpUrl() );
        assertEquals( LACNIC, servlet.makeIpBase( "/ip/191.0.1.0" ).getHttpUrl() );
        assertEquals( LACNIC, servlet.makeIpBase( "/ip/191.0.1.0/24" ).getHttpUrl() );
        assertEquals( ARIN, servlet.makeIpBase( "/ip/2620:0000:0000:0000:0000:0000:0000:0000" ).getHttpUrl() );
        //TODO renable when their server are put back in the bootstrap files
        //assertEquals( AFRINIC, servlet.makeIpBase( "/ip/2c00:0000::/12" ).getHttpUrl() );
        assertEquals( LACNIC, servlet.makeIpBase( "/ip/2800:0000::/12" ).getHttpUrl() );
        //TODO renable when their server are put back in the bootstrap files
        //assertEquals( IANA, servlet.makeIpBase( "/ip/2001:0000::1" ).getHttpUrl() );
    }

    @Test
    public void testMakeDomainBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( COM, servlet.makeDomainBase( "/domain/example.COM" ).getHttpUrl() );
        assertEquals( COM, servlet.makeDomainBase( "/domain/example.COM." ).getHttpUrl() );
        assertEquals( ARIN, servlet.makeDomainBase( "/domain/0.0.0.7.in-addr.arpa." ).getHttpUrl() );
        assertEquals( ARIN, servlet.makeDomainBase( "/domain/0.0.0.7.in-addr.arpa" ).getHttpUrl() );
        assertEquals( ARIN, servlet.makeDomainBase( "/domain/0.7.in-addr.arpa" ).getHttpUrl() );
        assertEquals( ARIN, servlet.makeDomainBase( "/domain/7.in-addr.arpa" ).getHttpUrl() );
        assertEquals( ARIN, servlet.makeDomainBase( "/domain/0.2.6.2.ip6.arpa" ).getHttpUrl() );
        //TODO renable when their server are put back in the bootstrap files
        //assertEquals( AFRINIC, servlet.makeDomainBase( "/domain/0.c.2.ip6.arpa" ).getHttpUrl() );
        assertEquals( LACNIC, servlet.makeDomainBase( "/domain/0.0.8.2.ip6.arpa" ).getHttpUrl() );
    }

    @Test
    public void testMakeNameserverBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( COM, servlet.makeNameserverBase( "/nameserver/ns1.example.COM" ).getHttpUrl() );
        assertEquals( COM, servlet.makeNameserverBase( "/nameserver/ns1.example.COM." ).getHttpUrl() );
    }

    @Test
    public void testMakeEntityBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( ARIN, servlet.makeEntityBase( "/entity/ABC123-ARIN" ).getHttpUrl() );
        assertEquals( RIPE, servlet.makeEntityBase( "/entity/ABC123-RIPE" ).getHttpUrl() );
        assertEquals( APNIC, servlet.makeEntityBase( "/entity/ABC123-AP" ).getHttpUrl() );
        assertEquals( LACNIC, servlet.makeEntityBase( "/entity/ABC123-LACNIC" ).getHttpUrl() );
        //TODO renable when their server are put back in the bootstrap files
        //assertEquals( AFRINIC, servlet.makeEntityBase( "/entity/ABC123-AFRINIC" ).getHttpUrl() );
    }
}
