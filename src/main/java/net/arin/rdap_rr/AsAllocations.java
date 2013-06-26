/*
 * Copyright (C) 2013 American Registry for Internet Numbers (ARIN)
 */
package net.arin.rdap_rr;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @version $Rev$, $Date$
 */
public class AsAllocations extends DefaultHandler
{
    private Record record = null;
    private String tempChars = null;
    private TreeMap<Long,String> allocations = new TreeMap<Long, String>(  );

    public void loadData()
        throws Exception
    {
        InputStream inputStream = getClass().getResourceAsStream( "/as_allocations.xml" );
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
        tempChars = new String( chars, start, length );
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
            String value = "(unknown)";
            if( record.description.equals( "Assigned by ARIN" ) )
            {
                value = "://rdap.arin.net";
            }
            else if( record.description.equals( "Assigned by RIPE NCC" ) )
            {
                value = "://rdap.ripe.net";
            }
            else if( record.description.equals( "Assigned by APNIC" ) )
            {
                value = "://rdap.apnic.net";
            }
            else if( record.description.equals( "Assigned by LACNIC" ) )
            {
                value = "://rdap.lacnic.net";
            }
            else if( record.description.equals( "Assigned by AFRINIC" ) )
            {
                value = "://rdap.afrinic.net";
            }
            else if( record.description.equals( "Reserved" ) )
            {
                value = "://rdap.iana.net";
            }
            else if( record.description.equals( "Unallocated" ) )
            {
                value = "://rdap.iana.net";
            }
            else if( record.description.equals( "AS_TRANS" ) )
            {
                value = "://rdap.iana.net";
            }
            if( !allocations.containsKey( key ) )
            {
                allocations.put( key, value );
            }
        }
    }

    public String getUrl( long number )
    {
        Entry<Long, String> entry = allocations.floorEntry( number );
        return entry.getValue();
    }

    class Record
    {
        public String number;
        public String description;
    }
}
