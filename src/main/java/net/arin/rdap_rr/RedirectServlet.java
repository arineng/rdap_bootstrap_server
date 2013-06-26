/*
 * Copyright (C) 2013 American Registry for Internet Numbers (ARIN)
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
            String url = makeRedirectUrl( req, "//rdap.iana.org" );
            resp.sendRedirect( url );
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
                    String url = makeRedirectUrl( req, "//rdap.iana.org" );
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
}
