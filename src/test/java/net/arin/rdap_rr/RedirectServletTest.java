/*
 * Copyright (C) 2013 American Registry for Internet Numbers (ARIN)
 */
package net.arin.rdap_rr;

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
}
