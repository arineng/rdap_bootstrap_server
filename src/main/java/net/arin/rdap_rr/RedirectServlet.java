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

import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * @version $Rev$, $Date$
 */
public class RedirectServlet extends HttpServlet
{
    private AsAllocations asAllocations = new AsAllocations();
    private IpV6Allocations ipV6Allocations = new IpV6Allocations();
    private IpV4Allocations ipV4Allocations = new IpV4Allocations();
    private TldAllocations tldAllocations = new TldAllocations();

    @Override
    public void init( ServletConfig config ) throws ServletException
    {
        try
        {
            asAllocations.loadData();
            ipV4Allocations.loadData();
            ipV6Allocations.loadData();
            tldAllocations.loadData();
        }
        catch ( Exception e )
        {
            throw new ServletException( e );
        }
    }

    @Override
    protected void service( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        String pathInfo = req.getPathInfo();
        if( pathInfo.startsWith( "/domain/" ) )
        {
            try
            {
                String base = makeDomainBase( pathInfo );
                if( base == null )
                {
                    resp.sendError( HttpServletResponse.SC_NOT_FOUND );
                }
                else
                {
                    String url = makeRedirectUrl( req, base );
                    resp.sendRedirect( url );
                }
            }
            catch ( Exception e )
            {
                resp.sendError( HttpServletResponse.SC_BAD_REQUEST, e.getMessage() );
            }
        }
        else if( pathInfo.startsWith( "/ip/" ) )
        {
            try
            {
                String base = makeIpBase( pathInfo );
                if( base == null )
                {
                    resp.sendError( HttpServletResponse.SC_NOT_FOUND );
                }
                else
                {
                    String url = makeRedirectUrl( req, base );
                    resp.sendRedirect( url );
                }
            }
            catch ( Exception e )
            {
                resp.sendError( HttpServletResponse.SC_BAD_REQUEST, e.getMessage() );
            }
        }
        else if( pathInfo.startsWith( "/autnum/" ) )
        {
            try
            {
                long autnum = makeAutNumLong( pathInfo );
                String base = asAllocations.getUrl( autnum );
                if( base == null )
                {
                    resp.sendError( HttpServletResponse.SC_NOT_FOUND );
                }
                else
                {
                    String url = makeRedirectUrl( req, base );
                    resp.sendRedirect( url );
                }
            }
            catch ( Exception e )
            {
                resp.sendError( HttpServletResponse.SC_BAD_REQUEST, e.getMessage() );
            }
        }
        else
        {
            resp.sendError( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    private String makeRedirectUrl( HttpServletRequest req, String base )
    {
        return req.getScheme() + base + req.getPathInfo();
    }

    public long makeAutNumLong( String pathInfo )
    {
        long autnum = Long.parseLong( pathInfo.split( "/" )[2] );
        return autnum;
    }

    public String makeIpBase( String pathInfo )
    {
        //strip leading "/ip/"
        pathInfo = pathInfo.substring( 4 );
        if( pathInfo.indexOf( ":" ) == -1 ) //is not ipv6
        {
            String firstOctet = pathInfo.split( "\\." )[ 0 ];
            return ipV4Allocations.getUrl( Integer.parseInt( firstOctet ) );
        }
        //else
        IPv6Address addr = null;
        if( pathInfo.indexOf( "/" ) == -1 )
        {
            addr = IPv6Address.fromString( pathInfo );
        }
        else
        {
            IPv6Network net = IPv6Network.fromString( pathInfo );
            addr = net.getFirst();
        }
        return ipV6Allocations.getUrl( addr );
    }

    public String makeDomainBase( String pathInfo )
    {
        //strip leading "/domain/"
        pathInfo = pathInfo.substring( 8 );
        //strip possible trailing period
        if( pathInfo.endsWith( "." ) )
        {
            pathInfo = pathInfo.substring( 0, pathInfo.length() - 1 );
        }
        if( pathInfo.endsWith( ".in-addr.arpa" ) )
        {
            String[] labels = pathInfo.split( "\\." );
            String firstOctet = labels[ labels.length -3 ];
            return ipV4Allocations.getUrl( Integer.parseInt( firstOctet ) );
        }
        else if( pathInfo.endsWith( ".ip6.arpa" ) )
        {
            String[] labels = pathInfo.split( "\\." );
            byte[] bytes = new byte[ 16 ];
            Arrays.fill( bytes, ( byte ) 0 );
            int labelIdx = labels.length -3;
            int byteIdx = 0;
            int idxJump = 1;
            while( labelIdx > 0 )
            {
                char ch = labels[ labelIdx ].charAt( 0 );
                byte value = 0;
                if( ch >= '0' && ch <= '9' )
                {
                    value = (byte)(ch - '0');
                }
                else if (ch >= 'A' && ch <= 'F' )
                {
                    value = (byte)(ch - ( 'A' - 0xaL ) );
                }
                else if (ch >= 'a' && ch <= 'f' )
                {
                    value = (byte)(ch - ( 'a' - 0xaL ) );
                }
                if( idxJump % 2 == 1 )
                {
                    bytes[ byteIdx ] = ( byte ) (value << 4);
                }
                else
                {
                    bytes[ byteIdx ] = ( byte ) (bytes[ byteIdx ] + value);
                }
                labelIdx--;
                idxJump++;
                if( idxJump % 2 == 1 )
                {
                    byteIdx++;
                }
            }
            return ipV6Allocations.getUrl( IPv6Address.fromByteArray( bytes ) );
        }
        //else
        String[] labels = pathInfo.split( "\\." );
        return tldAllocations.getUrl( labels[ labels.length -1 ] );
    }
}
