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

import net.arin.rdap_bootstrap.Constants;
import net.arin.rdap_bootstrap.service.JsonBootstrapFile.ServiceUrls;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class RedirectServletTest
{
    private static final String ARIN_HTTP = "http://rdap.arin.net/registry";
    private static final String LACNIC_HTTPS = "https://rdap.lacnic.net/rdap";
    private static final String APNIC_HTTPS = "https://rdap.apnic.net";
    private static final String RIPE_HTTP = "http://rdap.db.ripe.net";
    private static final String RIPE_HTTPS = "https://rdap.db.ripe.net";
    private static final String INFO_HTTP = "http://rdg.afilias.info/rdap";

    @Test
    public void testGetRedirectUrlDefault() throws Exception
    {
        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );

        ServiceUrls urls = new ServiceUrls();
        urls.addUrl( "http://example.com" );
        urls.addUrl( "https://example.com" );

        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "https://example.com/bar", servlet.getRedirectUrl( "http", "/bar", urls ) );
        assertEquals( "https://example.com/bar", servlet.getRedirectUrl( "https", "/bar", urls ) );
    }

    @Test
    public void testGetRedirectUrlDefaultOnlyHttp() throws Exception
    {
        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );

        ServiceUrls urls = new ServiceUrls();
        urls.addUrl( "http://example.com" );

        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "http://example.com/bar", servlet.getRedirectUrl( "http", "/bar", urls ) );
        assertEquals( "http://example.com/bar", servlet.getRedirectUrl( "https", "/bar", urls ) );
    }

    @Test
    public void testGetRedirectUrlDefaultOnlyHttps() throws Exception
    {
        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );

        ServiceUrls urls = new ServiceUrls();
        urls.addUrl( "https://example.com" );

        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "https://example.com/bar", servlet.getRedirectUrl( "http", "/bar", urls ) );
        assertEquals( "https://example.com/bar", servlet.getRedirectUrl( "https", "/bar", urls ) );
    }

    @Test
    public void testGetRedirectUrlFalse() throws Exception
    {
        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );
        System.setProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT, "False" );

        ServiceUrls urls = new ServiceUrls();
        urls.addUrl( "http://example.com" );
        urls.addUrl( "https://example.com" );

        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "https://example.com/bar", servlet.getRedirectUrl( "http", "/bar", urls ) );
        assertEquals( "https://example.com/bar", servlet.getRedirectUrl( "https", "/bar", urls ) );

        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );
    }

    @Test
    public void testGetRedirectUrlTrue() throws Exception
    {
        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );
        System.setProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT, "true" );

        ServiceUrls urls = new ServiceUrls();
        urls.addUrl( "http://example.com" );
        urls.addUrl( "https://example.com" );

        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "http://example.com/bar", servlet.getRedirectUrl( "http", "/bar", urls ) );
        assertEquals( "https://example.com/bar", servlet.getRedirectUrl( "https", "/bar", urls ) );

        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );
    }

    @Test
    public void testGetRedirectUrlTrueOnlyHttp() throws Exception
    {
        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );
        System.setProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT, "true" );

        ServiceUrls urls = new ServiceUrls();
        urls.addUrl( "http://example.com" );

        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "http://example.com/bar", servlet.getRedirectUrl( "http", "/bar", urls ) );
        assertEquals( "http://example.com/bar", servlet.getRedirectUrl( "https", "/bar", urls ) );

        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );
    }

    @Test
    public void testGetRedirectUrlTrueOnlyHttps() throws Exception
    {
        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );
        System.setProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT, "true" );

        ServiceUrls urls = new ServiceUrls();
        urls.addUrl( "https://example.com" );

        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( "https://example.com/bar", servlet.getRedirectUrl( "http", "/bar", urls ) );
        assertEquals( "https://example.com/bar", servlet.getRedirectUrl( "https", "/bar", urls ) );

        System.clearProperty( Constants.PROPERTY_PREFIX + RedirectServlet.MATCH_SCHEME_ON_REDIRECT );
    }

    @Test
    public void testMakeAutNumInt() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( ARIN_HTTP, servlet.makeAutnumBase( "/autnum/10" ).getHttpUrl() );
        assertEquals( RIPE_HTTPS, servlet.makeAutnumBase( "/autnum/42222" ).getHttpsUrl() );
    }

    @Test
    public void testMakeIpBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( ARIN_HTTP, servlet.makeIpBase( "/ip/7.0.0.0/8" ).getHttpUrl() );
        assertEquals( ARIN_HTTP, servlet.makeIpBase( "/ip/7.0.0.0/16" ).getHttpUrl() );
        assertEquals( LACNIC_HTTPS, servlet.makeIpBase( "/ip/191.0.1.0/24" ).getHttpsUrl() );
        assertEquals( ARIN_HTTP, servlet.makeIpBase( "/ip/2620:0000:0000:0000:0000:0000:0000:0000" ).getHttpUrl() );
        assertEquals( LACNIC_HTTPS, servlet.makeIpBase( "/ip/2800:0000::/12" ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, servlet.makeIpBase( "/ip/191.0.1.1/32" ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, servlet.makeIpBase( "/ip/191.0.1.1" ).getHttpsUrl() );
    }

    @Test
    public void testMakeDomainBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( INFO_HTTP, servlet.makeDomainBase( "/domain/example.INFO" ).getHttpUrl() );
        assertEquals( INFO_HTTP, servlet.makeDomainBase( "/domain/example.INFO." ).getHttpUrl() );
        assertEquals( ARIN_HTTP, servlet.makeDomainBase( "/domain/0.0.0.7.in-addr.arpa." ).getHttpUrl() );
        assertEquals( ARIN_HTTP, servlet.makeDomainBase( "/domain/0.0.0.7.in-addr.arpa" ).getHttpUrl() );
        assertEquals( ARIN_HTTP, servlet.makeDomainBase( "/domain/0.7.in-addr.arpa" ).getHttpUrl() );
        assertEquals( ARIN_HTTP, servlet.makeDomainBase( "/domain/7.in-addr.arpa" ).getHttpUrl() );
        assertEquals( ARIN_HTTP, servlet.makeDomainBase( "/domain/0.2.6.2.ip6.arpa" ).getHttpUrl() );
        assertEquals( LACNIC_HTTPS, servlet.makeDomainBase( "/domain/0.0.8.2.ip6.arpa" ).getHttpsUrl() );
    }

    @Test
    public void testMakeNameserverBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( INFO_HTTP, servlet.makeNameserverBase( "/nameserver/ns1.example.INFO" ).getHttpUrl() );
        assertEquals( INFO_HTTP, servlet.makeNameserverBase( "/nameserver/ns1.example.INFO." ).getHttpUrl() );
    }

    @Test
    public void testMakeEntityBase() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();
        servlet.init( null );

        assertEquals( ARIN_HTTP, servlet.makeEntityBase( "/entity/ABC123-ARIN" ).getHttpUrl() );
        assertEquals( RIPE_HTTP, servlet.makeEntityBase( "/entity/ABC123-RIPE" ).getHttpUrl() );
        assertEquals( APNIC_HTTPS, servlet.makeEntityBase( "/entity/ABC123-AP" ).getHttpsUrl() );
        assertEquals( LACNIC_HTTPS, servlet.makeEntityBase( "/entity/ABC123-LACNIC" ).getHttpsUrl() );
    }
}
