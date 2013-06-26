/*
 * Copyright (C) 2013 American Registry for Internet Numbers (ARIN)
 */
package net.arin.rdap_rr;

import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.HashMap;
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

    public void loadData()
        throws Exception
    {
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
        tempChars = new String( chars, start, length );
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
                value = "://rdap.arin.net";
            }
            else if( record.description.equals( "RIPE NCC" ) )
            {
                value = "://rdap.ripe.net";
            }
            else if( record.description.equals( "APNIC" ) )
            {
                value = "://rdap.apnic.net";
            }
            else if( record.description.equals( "LACNIC" ) )
            {
                value = "://rdap.lacnic.net";
            }
            else if( record.description.equals( "AFRINIC" ) )
            {
                value = "://rdap.afrinic.net";
            }
            else if( record.description.startsWith( "IANA" ) )
            {
                value = "://rdap.iana.net";
            }
            else if( record.status.equals( "RESERVED" ) )
            {
                value = "://rdap.iana.net";
            }
            allocations.put( key, value );
        }
    }

    public String getUrl( long prefix )
    {
        Map.Entry<Long,String> entry = allocations.floorEntry( prefix );
        if( entry != null )
        {
            return entry.getValue();
        }
        return null;
    }

    public String getUrl( IPv6Address addr )
    {
        return getUrl( addr.getHighBits() );
    }

    public String getUrl( IPv6Network net )
    {
        return getUrl( net.getFirst().getHighBits() );
    }

    class Record
    {
        public String prefix;
        public String description;
        public String status;
    }
}
