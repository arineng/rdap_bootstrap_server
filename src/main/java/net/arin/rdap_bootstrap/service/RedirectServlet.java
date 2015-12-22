/*
 * Copyright (C) 2013-2015 American Registry for Internet Numbers (ARIN)
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arin.rdap_bootstrap.Constants;
import net.arin.rdap_bootstrap.json.Notice;
import net.arin.rdap_bootstrap.json.Response;
import net.arin.rdap_bootstrap.service.DefaultBootstrap.Type;
import net.arin.rdap_bootstrap.service.JsonBootstrapFile.ServiceUrls;
import net.arin.rdap_bootstrap.service.ResourceFiles.BootFiles;
import net.arin.rdap_bootstrap.service.Statistics.UrlHits;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;

/**
 * @version $Rev$, $Date$
 */
public class RedirectServlet extends HttpServlet
{
    private AsBootstrap asBootstrap = new AsBootstrap();
    private IpV6Bootstrap ipV6Bootstrap = new IpV6Bootstrap();
    private IpV4Bootstrap ipV4Bootstrap = new IpV4Bootstrap();
    private DomainBootstrap domainBootstrap = new DomainBootstrap();
    private DefaultBootstrap defaultBootstrap = new DefaultBootstrap();
    private EntityBootstrap entityBootstrap = new EntityBootstrap();

    private volatile Statistics statistics;

    private ResourceFiles resourceFiles;
    private Timer timer;
    Boolean matchSchemeOnRedirect = Boolean.FALSE;

    private static final long CHECK_CONFIG_FILES = 60000L;
    static final String MATCH_SCHEME_ON_REDIRECT = "match_scheme_on_redirect";

    @Override
    public void init( ServletConfig config ) throws ServletException
    {
        super.init( config );

        statistics = new Statistics();

        matchSchemeOnRedirect = Boolean.valueOf( System
            .getProperty( Constants.PROPERTY_PREFIX + MATCH_SCHEME_ON_REDIRECT,
                matchSchemeOnRedirect.toString() ) );

        try
        {
            LoadConfigTask loadConfigTask = new LoadConfigTask();
            loadConfigTask.loadData();

            if ( config != null )
            {
                timer = new Timer();
                timer.schedule( loadConfigTask, CHECK_CONFIG_FILES, CHECK_CONFIG_FILES );
            }
        }
        catch ( Exception e )
        {
            throw new ServletException( e );
        }
    }

    protected void serve( UrlHits urlHits, BaseMaker baseMaker, DefaultBootstrap.Type defaultType,
                          String pathInfo, HttpServletRequest req, HttpServletResponse resp )
        throws IOException
    {
        try
        {
            UrlHits hits = urlHits;
            ServiceUrls urls = baseMaker.makeBase( pathInfo );
            if ( urls == null && defaultType != null )
            {
                urls = defaultBootstrap.getServiceUrls( defaultType );
                hits = UrlHits.DEFAULTHITS;
            }
            if ( urls == null )
            {
                resp.sendError( HttpServletResponse.SC_NOT_FOUND );
                statistics.getTotalMisses().incrementAndGet();
            }
            else
            {
                String redirectUrl = getRedirectUrl( req.getScheme(), req.getPathInfo(), urls );
                if ( hits != null )
                {
                    hits.hit( redirectUrl );
                }
                statistics.getTotalHits().incrementAndGet();
                resp.sendRedirect( redirectUrl );
            }
        }
        catch ( Exception e )
        {
            resp.sendError( HttpServletResponse.SC_BAD_REQUEST, e.getMessage() );
        }

    }

    String getRedirectUrl( String scheme, String pathInfo, ServiceUrls urls )
    {
        String redirectUrl = null;
        if ( matchSchemeOnRedirect )
        {
            if ( scheme.equals( "https" ) && urls.getHttpsUrl() != null )
            {
                redirectUrl = urls.getHttpsUrl() + pathInfo;
            }
            else if ( scheme.equals( "http" ) && urls.getHttpUrl() != null )
            {
                redirectUrl = urls.getHttpUrl() + pathInfo;
            }
            else
            {
                redirectUrl = urls.getUrls().get( 0 ) + pathInfo;
            }
        }
        else
        {
            redirectUrl = urls.getHttpsUrl();
            if ( redirectUrl == null )
            {
                redirectUrl = urls.getHttpUrl();
            }
            if ( redirectUrl != null )
            {
                redirectUrl += pathInfo;
            }
        }
        return redirectUrl;
    }

    @Override
    protected void service( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        String pathInfo = req.getPathInfo();
        if ( pathInfo.startsWith( "/domain/" ) )
        {
            serve( UrlHits.DOMAINHITS, new MakeDomainBase(), Type.DOMAIN, pathInfo, req, resp );
        }
        else if ( pathInfo.startsWith( "/nameserver/" ) )
        {
            serve( UrlHits.NAMESERVERHITS, new MakeNameserverBase(), Type.NAMESERVER, pathInfo, req,
                resp );
        }
        else if ( pathInfo.startsWith( "/ip/" ) )
        {
            serve( UrlHits.IPHITS, new MakeIpBase(), Type.IP, pathInfo, req, resp );
        }
        else if ( pathInfo.startsWith( "/entity/" ) )
        {
            serve( UrlHits.ENTITYHITS, new MakeEntityBase(), Type.ENTITY, pathInfo, req, resp );
        }
        else if ( pathInfo.startsWith( "/autnum/" ) )
        {
            serve( UrlHits.ASHITS, new MakeAutnumBase(), Type.AUTNUM, pathInfo, req, resp );
        }
        else if ( pathInfo.startsWith( "/help" ) )
        {
            resp.setContentType( "application/rdap+json" );
            makeHelp( resp.getOutputStream() );
        }
        else
        {
            resp.sendError( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    public interface BaseMaker
    {
        public ServiceUrls makeBase( String pathInfo );
    }

    public ServiceUrls makeAutnumBase( String pathInfo )
    {
        return new MakeAutnumBase().makeBase( pathInfo );
    }

    public class MakeAutnumBase implements BaseMaker
    {
        public ServiceUrls makeBase( String pathInfo )
        {
            return asBootstrap.getServiceUrls( pathInfo.split( "/" )[2] );
        }
    }

    public ServiceUrls makeIpBase( String pathInfo )
    {
        return new MakeIpBase().makeBase( pathInfo );
    }

    public class MakeIpBase implements BaseMaker
    {
        public ServiceUrls makeBase( String pathInfo )
        {
            // strip leading "/ip/"
            pathInfo = pathInfo.substring( 4 );
            if ( pathInfo.indexOf( ":" ) == -1 ) // is not ipv6
            {
                // String firstOctet = pathInfo.split( "\\." )[ 0 ];
                return ipV4Bootstrap.getServiceUrls( pathInfo );
            }
            // else
            IPv6Address addr = null;
            if ( pathInfo.indexOf( "/" ) == -1 )
            {
                addr = IPv6Address.fromString( pathInfo );
            }
            else
            {
                IPv6Network net = IPv6Network.fromString( pathInfo );
                addr = net.getFirst();
            }
            return ipV6Bootstrap.getServiceUrls( addr );
        }
    }

    public ServiceUrls makeDomainBase( String pathInfo )
    {
        return new MakeDomainBase().makeBase( pathInfo );
    }

    public class MakeDomainBase implements BaseMaker
    {
        public ServiceUrls makeBase( String pathInfo )
        {
            // strip leading "/domain/"
            pathInfo = pathInfo.substring( 8 );
            // strip possible trailing period
            if ( pathInfo.endsWith( "." ) )
            {
                pathInfo = pathInfo.substring( 0, pathInfo.length() - 1 );
            }
            if ( pathInfo.endsWith( ".in-addr.arpa" ) )
            {
                final int BITS_PER_WORD = 8, MIVAR = 1;
                final String DELIMITER = ".";

                String[] words = new String[4];
                Arrays.fill( words, "0" );

                final String[] _split = pathInfo.split( "\\." );
                int n = _split.length - 2;

                String s = "", _s = "";
                for ( int i = n - 1, j = 1; i >= 0; i--, j++ )
                {
                    _s += _split[i];
                    if ( j % MIVAR == 0 )
                    {
                        words[j / MIVAR - 1] = _s;// índice posicional
                        _s = "";
                    }
                }

                for ( int i = 0; i < words.length - 1; i++ )
                {// todos menos el
                    // último
                    s += words[i] + DELIMITER;
                }
                s += words[words.length - 1];// el último sin DELIMITER
                s += "/" + BITS_PER_WORD * n;

                return ipV4Bootstrap.getServiceUrls( s );

            }
            else if ( pathInfo.endsWith( ".ip6.arpa" ) )
            {
                String[] labels = pathInfo.split( "\\." );
                byte[] bytes = new byte[16];
                Arrays.fill( bytes, ( byte ) 0 );
                int labelIdx = labels.length - 3;
                int byteIdx = 0;
                int idxJump = 1;
                while ( labelIdx > 0 )
                {
                    char ch = labels[labelIdx].charAt( 0 );
                    byte value = 0;
                    if ( ch >= '0' && ch <= '9' )
                    {
                        value = ( byte ) ( ch - '0' );
                    }
                    else if ( ch >= 'A' && ch <= 'F' )
                    {
                        value = ( byte ) ( ch - ( 'A' - 0xaL ) );
                    }
                    else if ( ch >= 'a' && ch <= 'f' )
                    {
                        value = ( byte ) ( ch - ( 'a' - 0xaL ) );
                    }
                    if ( idxJump % 2 == 1 )
                    {
                        bytes[byteIdx] = ( byte ) ( value << 4 );
                    }
                    else
                    {
                        bytes[byteIdx] = ( byte ) ( bytes[byteIdx] + value );
                    }
                    labelIdx--;
                    idxJump++;
                    if ( idxJump % 2 == 1 )
                    {
                        byteIdx++;
                    }
                }
                return ipV6Bootstrap.getServiceUrls( IPv6Address.fromByteArray( bytes ) );
            }
            // else
            String[] labels = pathInfo.split( "\\." );
            return domainBootstrap.getServiceUrls( labels[labels.length - 1] );
        }

    }

    public ServiceUrls makeNameserverBase( String pathInfo )
    {
        return new MakeNameserverBase().makeBase( pathInfo );
    }

    public class MakeNameserverBase implements BaseMaker
    {
        public ServiceUrls makeBase( String pathInfo )
        {
            // strip leading "/nameserver/"
            pathInfo = pathInfo.substring( 12 );
            // strip possible trailing period
            if ( pathInfo.endsWith( "." ) )
            {
                pathInfo = pathInfo.substring( 0, pathInfo.length() - 1 );
            }
            String[] labels = pathInfo.split( "\\." );
            return domainBootstrap.getServiceUrls( labels[labels.length - 1] );
        }
    }

    public ServiceUrls makeEntityBase( String pathInfo )
    {
        return new MakeEntityBase().makeBase( pathInfo );
    }

    public class MakeEntityBase implements BaseMaker
    {
        public ServiceUrls makeBase( String pathInfo )
        {
            int i = pathInfo.lastIndexOf( '-' );
            if ( i != -1 && i + 1 < pathInfo.length() )
            {
                return entityBootstrap.getServiceUrls( pathInfo.substring( i + 1 ) );
            }
            // else
            return null;
        }

    }

    private Notice makeStatsNotice( Statistics.UrlHits stats )
    {
        Notice notice = new Notice();
        notice.setTitle( stats.getTitle() );
        ArrayList<String> description = new ArrayList<String>();
        Set<Entry<String, AtomicLong>> entrySet = stats.getEntrySet();
        if ( entrySet.size() != 0 )
        {
            for ( Entry<String, AtomicLong> entry : entrySet )
            {
                description
                    .add( String.format( "%-5d = %25s", entry.getValue().get(), entry.getKey() ) );
            }
        }
        else
        {
            description.add( "Zero queries." );
        }
        notice.setDescription( description.toArray( new String[description.size()] ) );
        return notice;
    }

    public void makeHelp( OutputStream outputStream ) throws IOException
    {
        Response response = new Response( null );
        ArrayList<Notice> notices = new ArrayList<Notice>();

        // do statistics
        for ( Statistics.UrlHits stats : Statistics.UrlHits.values() )
        {
            notices.add( makeStatsNotice( stats ) );
        }

        // totals
        Notice notice = new Notice();
        notice.setTitle( "Totals" );
        String[] description = new String[2];
        description[0] = String.format( "Hits   = %5d", statistics.getTotalHits().get() );
        description[1] = String.format( "Misses = %5d", statistics.getTotalMisses().get() );
        notice.setDescription( description );
        notices.add( notice );

        // Modified dates for various bootstrap files, done this way so that
        // Publication dates can be published as well.
        notices.add( createPublicationDateNotice( "Default",
            resourceFiles.getLastModified( BootFiles.DEFAULT.getKey() ),
            defaultBootstrap.getPublication() ) );
        notices.add( createPublicationDateNotice( "As",
            resourceFiles.getLastModified( BootFiles.AS.getKey() ),
            asBootstrap.getPublication() ) );
        notices.add( createPublicationDateNotice( "Domain",
            resourceFiles.getLastModified( BootFiles.DOMAIN.getKey() ),
            domainBootstrap.getPublication() ) );
        notices.add( createPublicationDateNotice( "Entity",
            resourceFiles.getLastModified( BootFiles.ENTITY.getKey() ),
            entityBootstrap.getPublication() ) );
        notices.add( createPublicationDateNotice( "IpV4",
            resourceFiles.getLastModified( BootFiles.V4.getKey() ),
            ipV4Bootstrap.getPublication() ) );
        notices.add( createPublicationDateNotice( "IpV6",
            resourceFiles.getLastModified( BootFiles.V6.getKey() ),
            ipV6Bootstrap.getPublication() ) );

        response.setNotices( notices );

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion( Include.NON_EMPTY );
        ObjectWriter writer = mapper.writer( new DefaultPrettyPrinter() );
        writer.writeValue( outputStream, response );
    }

    private Notice createPublicationDateNotice( String file, long lastModified,
                                                String publicationDate )
    {
        Notice bootFileModifiedNotice = new Notice();

        bootFileModifiedNotice
            .setTitle( String.format( "%s Bootstrap File Modified and Published Dates", file ) );
        String[] bootFileModifiedDescription = new String[2];
        // Date format as 2015-05-15T17:04:06-0500 (Y-m-d'T'H:M:Sz)
        bootFileModifiedDescription[0] = String.format( "%1$tFT%1$tT%1$tz", lastModified );
        bootFileModifiedDescription[1] = publicationDate;
        bootFileModifiedNotice.setDescription( bootFileModifiedDescription );

        return bootFileModifiedNotice;
    }

    private class LoadConfigTask extends TimerTask
    {
        private boolean isModified( long currentTime, long lastModified )
        {
            if ( ( currentTime - CHECK_CONFIG_FILES ) < lastModified )
            {
                return true;
            }
            // else
            return false;
        }

        @Override
        public void run()
        {
            boolean load = false;
            long currentTime = System.currentTimeMillis();
            for ( BootFiles bootFiles : BootFiles.values() )
            {
                if ( isModified( currentTime,
                    resourceFiles.getLastModified( bootFiles.getKey() ) ) )
                {
                    getServletContext().log( String
                        .format( "%s was last modified at %s", bootFiles.getKey(),
                            new Date( resourceFiles.getLastModified( bootFiles.getKey() ) ) ) );
                    load = true;
                }
            }
            if ( load )
            {
                try
                {
                    loadData();
                }
                catch ( Exception e )
                {
                    getServletContext().log( "Problem loading config", e );
                }
            }
        }

        public void loadData() throws Exception
        {
            if ( getServletConfig() != null )
            {
                getServletContext().log( "Loading resource files." );
            }
            resourceFiles = new ResourceFiles();
            asBootstrap.loadData( resourceFiles );
            ipV4Bootstrap.loadData( resourceFiles );
            ipV6Bootstrap.loadData( resourceFiles );
            domainBootstrap.loadData( resourceFiles );
            entityBootstrap.loadData( resourceFiles );
            defaultBootstrap.loadData( resourceFiles );
        }
    }
}
