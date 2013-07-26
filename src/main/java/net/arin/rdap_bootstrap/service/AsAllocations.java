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
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @version $Rev$, $Date$
 */
public class AsAllocations extends DefaultHandler
{
    private Record record = null;
    private String tempChars = null;
    private volatile TreeMap<Long,String> allocations = new TreeMap<Long, String>(  );
    private TreeMap<Long,String> _allocations;
    private RirMap rirMap = new RirMap();

    public void loadData( ResourceFiles resourceFiles )
        throws Exception
    {
        rirMap.loadData( resourceFiles );
        _allocations = new TreeMap<Long, String>(  );
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        sp.parse( resourceFiles.getInputStream( ResourceFiles.AS_ALLOCATIONS ), this );
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
        if( qName.equals( "number" ) )
        {
            record.number = tempChars;
        }
        else if( qName.equals( "description" ) )
        {
            record.description = tempChars;
        }
        else if( qName.equals( "record" ) )
        {
            long key = Long.parseLong( record.number.split( "-" )[0] );
            String value = rirMap.getRirUrl( "IANA" );
            if( record.description.equals( "Assigned by ARIN" ) )
            {
                value = rirMap.getRirUrl( "ARIN" );
            }
            else if( record.description.equals( "Assigned by RIPE NCC" ) )
            {
                value = rirMap.getRirUrl( "RIPE" );
            }
            else if( record.description.equals( "Assigned by APNIC" ) )
            {
                value = rirMap.getRirUrl( "APNIC" );
            }
            else if( record.description.equals( "Assigned by LACNIC" ) )
            {
                value = rirMap.getRirUrl( "LACNIC" );
            }
            else if( record.description.equals( "Assigned by AFRINIC" ) )
            {
                value = rirMap.getRirUrl( "AFRINIC" );
            }
            if( !_allocations.containsKey( key ) )
            {
                _allocations.put( key, value );
            }
        }
    }

    public String getUrl( long number )
    {
        return getUrl( number, null );
    };

    public String getUrl( long number, HitCounter hitCounter )
    {
        Entry<Long, String> entry = allocations.floorEntry( number );
        if( hitCounter != null )
        {
            hitCounter.incrementCounter( entry.getValue() );
        }
        return entry.getValue();
    }

    public void addAsCountersToStatistics( Statistics stats )
    {
        for ( String s : allocations.values() )
        {
            stats.addAsRirCounter( rirMap.getRirFromUrl( s ) );
        }
    }

    class Record
    {
        public String number;
        public String description;
    }
}
