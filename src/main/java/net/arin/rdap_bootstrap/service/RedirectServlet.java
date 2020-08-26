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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import net.arin.rdap_bootstrap.service.JsonBootstrapFile.ServiceUrls;
import net.arin.rdap_bootstrap.service.ResourceFiles.BootFiles;
import net.arin.rdap_bootstrap.service.Statistics.UrlHits;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;
import org.apache.commons.io.FileUtils;

public class RedirectServlet extends HttpServlet
{
    private final DomainBootstrap domainBootstrap = new DomainBootstrap();
    private final IpV6Bootstrap ipV6Bootstrap = new IpV6Bootstrap();
    private final IpV4Bootstrap ipV4Bootstrap = new IpV4Bootstrap();
    private final AsBootstrap asBootstrap = new AsBootstrap();
    private final EntityBootstrap entityBootstrap = new EntityBootstrap();

    private volatile Statistics statistics;

    private ResourceFiles resourceFiles;
    Boolean matchSchemeOnRedirect = Boolean.FALSE;

    private static final long CHECK_CONFIG_FILES = 60000L; // every 1 minute
    static final String MATCH_SCHEME_ON_REDIRECT = "match_scheme_on_redirect";

    @Override
    public void init( ServletConfig config ) throws ServletException
    {
        super.init( config );

        statistics = new Statistics();

        matchSchemeOnRedirect = Boolean.valueOf( System.getProperty(
                Constants.PROPERTY_PREFIX + MATCH_SCHEME_ON_REDIRECT, matchSchemeOnRedirect.toString() ) );

        try
        {
            LoadConfigTask loadConfigTask = new LoadConfigTask();
            loadConfigTask.loadData();

            if ( config != null )
            {
                Timer timer = new Timer();
                timer.schedule( loadConfigTask, CHECK_CONFIG_FILES, CHECK_CONFIG_FILES );
            }
        }
        catch ( Exception e )
        {
            throw new ServletException( e );
        }

        // TODO:
        //   Download bootstrap files only if download_bootstrap_files property is set to true.
        //   Schedule as properties. Start after a day. Once a day subsequently.

        try
        {
            DownloadBootstrapFilesTask downloadBootstrapFilesTask = new DownloadBootstrapFilesTask();
            Timer timer = new Timer();
            timer.schedule( downloadBootstrapFilesTask, 10000L, 120000L );
        }
        catch ( Exception e )
        {
            throw new ServletException( e );
        }
    }

    protected void serve( UrlHits urlHits, BaseMaker baseMaker, String pathInfo, HttpServletRequest req,
                          HttpServletResponse resp )
            throws IOException
    {
        try
        {
            ServiceUrls urls = baseMaker.makeBase( pathInfo );
            if ( urls == null )
            {
                resp.sendError( HttpServletResponse.SC_NOT_FOUND );
                statistics.getTotalMisses().incrementAndGet();
            }
            else
            {
                String redirectUrl = getRedirectUrl( req.getScheme(), req.getPathInfo(), urls );
                if ( urlHits != null )
                {
                    urlHits.hit( redirectUrl );
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
        String redirectUrl;
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
            throws IOException
    {
        if ( req == null )
        {
            resp.sendError( HttpServletResponse.SC_BAD_REQUEST );
        }
        else if ( req.getPathInfo() == null )
        {
            resp.sendError( HttpServletResponse.SC_BAD_REQUEST );
        }
        else
        {
            String pathInfo = req.getPathInfo();
            if ( pathInfo.startsWith( "/domain/" ) )
            {
                serve( UrlHits.DOMAINHITS, new MakeDomainBase(), pathInfo, req, resp );
            }
            // The /nameserver path leverages the domain bootstrap logic to provide redirection for the nameserver
            // queries.
            else if ( pathInfo.startsWith( "/nameserver/" ) )
            {
                serve( UrlHits.NAMESERVERHITS, new MakeNameserverBase(), pathInfo, req, resp );
            }
            else if ( pathInfo.startsWith( "/ip/" ) )
            {
                serve( UrlHits.IPHITS, new MakeIpBase(), pathInfo, req, resp );
            }
            else if ( pathInfo.startsWith( "/autnum/" ) )
            {
                serve( UrlHits.ASHITS, new MakeAutnumBase(), pathInfo, req, resp );
            }
            // The /entity path provides redirection for the RIR entity queries.
            else if ( pathInfo.startsWith( "/entity/" ) )
            {
                serve( UrlHits.ENTITYHITS, new MakeEntityBase(), pathInfo, req, resp );
            }
            // The /help path returns statistics for ARIN's RDAP Bootstrap service.
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
    }

    public interface BaseMaker
    {
        ServiceUrls makeBase( String pathInfo );
    }

    // Domain names.

    public ServiceUrls makeDomainBase( String pathInfo )
    {
        return new MakeDomainBase().makeBase( pathInfo );
    }

    public class MakeDomainBase implements BaseMaker
    {
        public ServiceUrls makeBase( String pathInfo )
        {
            // Strip leading "/domain/".
            pathInfo = pathInfo.substring( 8 );
            // Strip possible trailing period.
            if ( pathInfo.endsWith( "." ) )
            {
                pathInfo = pathInfo.substring( 0, pathInfo.length() - 1 );
            }
            if ( pathInfo.endsWith( ".in-addr.arpa" ) )
            {
                final int bitsPerWord = 8;
                final int divisor = 1;
                final String delimiter = ".";

                String[] words = new String[4];
                Arrays.fill( words, "0" );

                final String[] _split = pathInfo.split( "\\." );
                int n = _split.length - 2;

                StringBuilder s = new StringBuilder();
                StringBuilder _s = new StringBuilder();
                for ( int i = n - 1, j = 1; i >= 0; i--, j++ )
                {
                    _s.append( _split[i] );
                    words[j / divisor - 1] = _s.toString();
                    _s = new StringBuilder();
                }

                // Get the CIDR string (prefix slash prefix length) to query the IPv4 bootstrap.
                for ( int i = 0; i < words.length - 1; i++ )
                {
                    s.append( words[i] ).append( delimiter );
                }
                s.append( words[words.length - 1] );
                s.append( "/" ).append( bitsPerWord * n );
                return ipV4Bootstrap.getServiceUrls( s.toString() );
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
            // else a forward domain
            String[] labels = pathInfo.split( "\\." );
            return domainBootstrap.getServiceUrls( labels[labels.length - 1] );
        }
    }

    // Nameservers. Only for forward domains.

    public ServiceUrls makeNameserverBase( String pathInfo )
    {
        return new MakeNameserverBase().makeBase( pathInfo );
    }

    public class MakeNameserverBase implements BaseMaker
    {
        public ServiceUrls makeBase( String pathInfo )
        {
            // Strip leading "/nameserver/".
            pathInfo = pathInfo.substring( 12 );
            // Strip possible trailing period.
            if ( pathInfo.endsWith( "." ) )
            {
                pathInfo = pathInfo.substring( 0, pathInfo.length() - 1 );
            }
            String[] labels = pathInfo.split( "\\." );
            return domainBootstrap.getServiceUrls( labels[labels.length - 1] );
        }
    }

    // IP addresses.

    public ServiceUrls makeIpBase( String pathInfo )
    {
        return new MakeIpBase().makeBase( pathInfo );
    }

    public class MakeIpBase implements BaseMaker
    {
        public ServiceUrls makeBase( String pathInfo )
        {
            // Strip leading "/ip/".
            pathInfo = pathInfo.substring( 4 );
            if ( !pathInfo.contains( ":" ) ) // is not IPv6
            {
                return ipV4Bootstrap.getServiceUrls( pathInfo );
            }
            // else
            IPv6Address addr;
            if ( !pathInfo.contains( "/" ) )
            {
                addr = IPv6Address.fromString( pathInfo );
                return ipV6Bootstrap.getServiceUrls( addr );
            }
            else
            {
                IPv6Network net = IPv6Network.fromString( pathInfo );
                return ipV6Bootstrap.getServiceUrls( net );
            }
        }
    }

    // AS numbers.

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

    // Entities.

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
                // Use the RIR label in the entity handle to get the redirection URL.
                return entityBootstrap.getServiceUrls( pathInfo.substring( i + 1 ) );
            }
            // else
            return null;
        }
    }

    // Statistics.

    private Notice makeStatsNotice( Statistics.UrlHits stats )
    {
        Notice notice = new Notice();
        notice.setTitle( stats.getTitle() );
        ArrayList<String> description = new ArrayList<>();
        Set<Entry<String, AtomicLong>> entrySet = stats.getEntrySet();
        if ( entrySet.size() != 0 )
        {
            for ( Entry<String, AtomicLong> entry : entrySet )
            {
                description.add( String.format( "%-5d = %25s", entry.getValue().get(), entry.getKey() ) );
            }
        }
        else
        {
            description.add( "Zero queries." );
        }
        notice.setDescription( description.toArray( new String[0] ) );
        return notice;
    }

    public void makeHelp( OutputStream outputStream ) throws IOException
    {
        Response response = new Response( null );
        ArrayList<Notice> notices = new ArrayList<>();

        // Do statistics.
        for ( Statistics.UrlHits stats : Statistics.UrlHits.values() )
        {
            notices.add( makeStatsNotice( stats ) );
        }

        // Totals.
        Notice notice = new Notice();
        notice.setTitle( "Totals" );
        String[] description = new String[2];
        description[0] = String.format( "Hits   = %5d", statistics.getTotalHits().get() );
        description[1] = String.format( "Misses = %5d", statistics.getTotalMisses().get() );
        notice.setDescription( description );
        notices.add( notice );

        // Modified dates for various bootstrap files. Done this way so that Publication dates can be published as well.
        notices.add( createPublicationDateNotice( "Domain",
                resourceFiles.getLastModified( BootFiles.DOMAIN.getKey() ),
                domainBootstrap.getPublication() ) );
        notices.add( createPublicationDateNotice( "IPv4",
                resourceFiles.getLastModified( BootFiles.V4.getKey() ),
                ipV4Bootstrap.getPublication() ) );
        notices.add( createPublicationDateNotice( "IPv6",
                resourceFiles.getLastModified( BootFiles.V6.getKey() ),
                ipV6Bootstrap.getPublication() ) );
        notices.add( createPublicationDateNotice( "AS",
                resourceFiles.getLastModified( BootFiles.AS.getKey() ),
                asBootstrap.getPublication() ) );
        notices.add( createPublicationDateNotice( "Entity",
                resourceFiles.getLastModified( BootFiles.ENTITY.getKey() ),
                entityBootstrap.getPublication() ) );

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

        bootFileModifiedNotice.setTitle( String.format( "%s Bootstrap File Modified and Published Dates", file ) );
        String[] bootFileModifiedDescription = new String[2];
        // Date format as 2015-05-15T17:04:06-0500 (Y-m-d'T'H:M:Sz).
        bootFileModifiedDescription[0] = String.format( "%1$tFT%1$tT%1$tz", lastModified );
        bootFileModifiedDescription[1] = publicationDate;
        bootFileModifiedNotice.setDescription( bootFileModifiedDescription );

        return bootFileModifiedNotice;
    }

    private class LoadConfigTask extends TimerTask
    {
        private boolean isModified( long currentTime, long lastModified )
        {
            return ( currentTime - CHECK_CONFIG_FILES ) < lastModified;
        }

        @Override
        public void run()
        {
            boolean load = false;
            long currentTime = System.currentTimeMillis();
            for ( BootFiles bootFiles : BootFiles.values() )
            {
                if ( isModified( currentTime, resourceFiles.getLastModified( bootFiles.getKey() ) ) )
                {
                    getServletContext().log( String.format( "%s was last modified at %s", bootFiles.getKey(),
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
                getServletContext().log( "Loading resource files" );
            }
            resourceFiles = new ResourceFiles();
            domainBootstrap.loadData( resourceFiles );
            ipV4Bootstrap.loadData( resourceFiles );
            ipV6Bootstrap.loadData( resourceFiles );
            asBootstrap.loadData( resourceFiles );
            entityBootstrap.loadData( resourceFiles );
        }
    }

    // TODO:
    //  Specify the download dir as a property.
    //  Specify the base download URL as a property.

    private class DownloadBootstrapFilesTask extends TimerTask
    {
        @Override
        public void run()
        {
            try
            {
                getServletContext().log( "Downloading files from IANA RDAP Bootstrap registry" );

                String downloadDir = "/Users/jasdips/misc/rdapbootstrap";
                Path downloadDirPath = Paths.get( downloadDir );
                Files.createDirectories( downloadDirPath );

                String baseDownloadURL = "https://data.iana.org/rdap";

                downloadFileSafely( baseDownloadURL, downloadDir, "asn.json" );
                downloadFileSafely( baseDownloadURL, downloadDir, "dns.json" );
                downloadFileSafely( baseDownloadURL, downloadDir, "ipv4.json" );
                downloadFileSafely( baseDownloadURL, downloadDir, "ipv6.json" );
            }
            catch ( IOException e )
            {
                getServletContext().log( "Problem downloading files from IANA RDAP Bootstrap registry", e );
            }
        }

        private void downloadFileSafely( String baseDownloadURL, String downloadDir, String fileName )
                throws IOException
        {
            getServletContext().log( "Downloading " + fileName );

            Path filePath = Paths.get( downloadDir + "/" + fileName );
            String newFilePathname = downloadDir + "/" + fileName + ".new";
            Path newFilePath = Paths.get( newFilePathname );
            Path curFilePath = Paths.get( downloadDir + "/" + fileName + ".cur" );
            Path oldFilePath = Paths.get( downloadDir + "/" + fileName + ".old" );

            FileUtils.copyURLToFile( new URL( baseDownloadURL + "/" + fileName ),
                    new File( newFilePathname ), 10000, 10000 );

            Files.deleteIfExists( oldFilePath );

            if ( Files.exists( curFilePath ) )
            {
                Files.copy( curFilePath, oldFilePath, StandardCopyOption.REPLACE_EXISTING );
                Files.deleteIfExists( filePath );
                Files.createSymbolicLink( filePath, oldFilePath );
            }

            Files.copy( newFilePath, curFilePath, StandardCopyOption.REPLACE_EXISTING );
            Files.deleteIfExists( filePath );
            Files.createSymbolicLink( filePath, curFilePath );
        }
    }
}
