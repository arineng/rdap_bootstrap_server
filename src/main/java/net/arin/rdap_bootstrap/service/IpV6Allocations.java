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

import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * @version $Rev$, $Date$
 */
public class IpV6Allocations extends DefaultHandler
{
    private Record record = null;
    private String tempChars = null;
    private TreeMap<Long,String> allocations = new TreeMap<Long, String>(  );
    private RirMap rirMap = new RirMap();

    public void loadData()
        throws Exception
    {
        rirMap.loadData();
        InputStream inputStream = getClass().getResourceAsStream( "/v6_allocations.xml" );
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        sp.parse( inputStream, this );
    }

    @Override
    public void startElement( String uri, String localName, String qName, Attributes attributes )
        throws SAXException
    {
        tempChars = "";
        if( qName.equals( "record" ) )
        {
            record = new Record();
        }
    }

    @Override
    public void characters( char[] chars, int start, int length ) throws SAXException
    {
        tempChars = tempChars + new String( chars, start, length );
    }

    @Override
    public void endElement( String uri, String localName, String qName ) throws SAXException
    {
        if( qName.equals( "prefix" ) )
        {
            record.prefix = tempChars;
        }
        else if( qName.equals( "description" ) )
        {
            if( record != null )
            {
                record.description = tempChars;
            }
        }
        else if( qName.equals( "status" ) )
        {
            record.status = tempChars;
        }
        else if( qName.equals( "record" ) )
        {
            IPv6Network v6net = IPv6Network.fromString( record.prefix );
            long key = v6net.getFirst().getHighBits();
            String value = "(unknown)";
            if( record.description.equals( "ARIN" ) )
            {
                value = rirMap.getRirUrl( "ARIN" );
            }
            else if( record.description.equals( "RIPE NCC" ) )
            {
                value = rirMap.getRirUrl( "RIPE" );
            }
            else if( record.description.equals( "APNIC" ) )
            {
                value = rirMap.getRirUrl( "APNIC" );
            }
            else if( record.description.equals( "LACNIC" ) )
            {
                value = rirMap.getRirUrl( "LACNIC" );
            }
            else if( record.description.equals( "AFRINIC" ) )
            {
                value = rirMap.getRirUrl( "AFRINIC" );
            }
            else if( record.description.startsWith( "IANA" ) )
            {
                value = rirMap.getRirUrl( "IANA" );
            }
            else if( record.status.equals( "RESERVED" ) )
            {
                value = rirMap.getRirUrl( "IANA" );
            }
            allocations.put( key, value );
        }
    }

    public String getUrl( long prefix, HitCounter hitCounter )
    {
        String retval = null;
        Map.Entry<Long,String> entry = allocations.floorEntry( prefix );
        if( entry != null )
        {
            retval = entry.getValue();
            if( hitCounter != null )
            {
                hitCounter.incrementCounter( retval );
            }
        }
        return retval;
    }

    public String getUrl( IPv6Address addr, HitCounter hitCounter )
    {
        return getUrl( addr.getHighBits(), hitCounter );
    }

    public String getUrl( IPv6Address addr )
    {
        return getUrl( addr.getHighBits(), null );
    }

    public String getUrl( IPv6Network net, HitCounter hitCounter )
    {
        return getUrl( net.getFirst().getHighBits(), hitCounter );
    }

    public String getUrl( IPv6Network net )
    {
        return getUrl( net.getFirst().getHighBits(), null );
    }

    public void addIp6CountersToStatistics( Statistics stats )
    {
        for ( String s : allocations.values() )
        {
            stats.addIp6RirCounter( s );
        }
    }

    public void addDomainRirCountersToStatistics( Statistics stats )
    {
        for ( String s : allocations.values() )
        {
            stats.addDomainRirCounter( s );
        }
    }

    class Record
    {
        public String prefix;
        public String description;
        public String status;
    }
}
