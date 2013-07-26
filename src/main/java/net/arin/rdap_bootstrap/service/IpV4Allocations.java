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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @version $Rev$, $Date$
 */
public class IpV4Allocations extends DefaultHandler
{
    private Record record = null;
    private String tempChars = null;
    private volatile HashMap<Integer,String> allocations = new HashMap<Integer, String>(  );
    private HashMap<Integer,String> _allocations;
    private RirMap rirMap = new RirMap();

    public void loadData( ResourceFiles resourceFiles )
        throws Exception
    {
        rirMap.loadData( resourceFiles );
        _allocations = new HashMap<Integer, String>(  );
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        sp.parse( resourceFiles.getInputStream( ResourceFiles.V4_ALLOCATIONS ), this );
        allocations = _allocations;
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
        else if( qName.equals( "designation" ) )
        {
            record.designation = tempChars;
        }
        else if( qName.equals( "status" ) )
        {
            record.status = tempChars;
        }
        else if( qName.equals( "record" ) )
        {
            int key = Integer.parseInt( record.prefix.split( "/" )[0] );
            String value = rirMap.getRirUrl( "IANA" );
            if( record.designation.equals( "ARIN" ) )
            {
                value = rirMap.getRirUrl( "ARIN" );
            }
            else if( record.designation.equals( "Administered by ARIN" ) )
            {
                value = rirMap.getRirUrl( "ARIN" );
            }
            else if( record.designation.equals( "RIPE NCC" ) )
            {
                value = rirMap.getRirUrl( "RIPE" );
            }
            else if( record.designation.equals( "Administered by RIPE NCC" ) )
            {
                value = rirMap.getRirUrl( "RIPE" );
            }
            else if( record.designation.equals( "APNIC" ) )
            {
                value = rirMap.getRirUrl( "APNIC" );
            }
            else if( record.designation.equals( "Administered by APNIC" ) )
            {
                value = rirMap.getRirUrl( "APNIC" );
            }
            else if( record.designation.equals( "LACNIC" ) )
            {
                value = rirMap.getRirUrl( "LACNIC" );
            }
            else if( record.designation.equals( "Administered by LACNIC" ) )
            {
                value = rirMap.getRirUrl( "LACNIC" );
            }
            else if( record.designation.equals( "AFRINIC" ) )
            {
                value = rirMap.getRirUrl( "AFRINIC" );
            }
            else if( record.designation.equals( "Administered by AFRINIC" ) )
            {
                value = rirMap.getRirUrl( "AFRINIC" );
            }
            else if( record.status.equals( "LEGACY" ) )
            {
                value = rirMap.getRirUrl( "ARIN" );
            }
            if( value != null )
            {
                _allocations.put( key, value );
            }
        }
    }

    public String getUrl( int prefix )
    {
        return getUrl( prefix, null );
    }

    public String getUrl( int prefix, HitCounter hitCounter )
    {
        String retval =  allocations.get( prefix );
        if( retval != null && hitCounter != null )
        {
            hitCounter.incrementCounter( retval );
        }
        return retval;
    }

    public void addIp4CountersToStatistics( Statistics stats )
    {
        for ( String s : allocations.values() )
        {
            stats.addIp4RirCounter( rirMap.getRirFromUrl( s ) );
        }
    }

    public void addDomainRirCountersToStatistics( Statistics stats )
    {
        for ( String s : allocations.values() )
        {
            stats.addDomainRirCounter( rirMap.getRirFromUrl( s ) );
        }
    }

    class Record
    {
        public String prefix;
        public String designation;
        public String status;
    }
}
