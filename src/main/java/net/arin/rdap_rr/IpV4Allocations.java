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
import java.util.HashMap;

/**
 * @version $Rev$, $Date$
 */
public class IpV4Allocations extends DefaultHandler
{
    private Record record = null;
    private String tempChars = null;
    private HashMap<Integer,String> allocations = new HashMap<Integer, String>(  );

    public void loadData()
        throws Exception
    {
        InputStream inputStream = getClass().getResourceAsStream( "/v4_allocations.xml" );
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
            String value = "(unknown)";
            if( record.designation.equals( "ARIN" ) )
            {
                value = "http://rdap.arin.net";
            }
            else if( record.designation.equals( "Administered by ARIN" ) )
            {
                value = "http://rdap.arin.net";
            }
            else if( record.designation.equals( "RIPE NCC" ) )
            {
                value = "http://rdap.ripe.net";
            }
            else if( record.designation.equals( "Administered by RIPE NCC" ) )
            {
                value = "http://rdap.ripe.net";
            }
            else if( record.designation.equals( "APNIC" ) )
            {
                value = "http://rdap.apnic.net";
            }
            else if( record.designation.equals( "Administered by APNIC" ) )
            {
                value = "http://rdap.apnic.net";
            }
            else if( record.designation.equals( "LACNIC" ) )
            {
                value = "http://rdap.lacnic.net";
            }
            else if( record.designation.equals( "Administered by LACNIC" ) )
            {
                value = "http://rdap.lacnic.net";
            }
            else if( record.designation.equals( "AFRINIC" ) )
            {
                value = "http://rdap.afrinic.net";
            }
            else if( record.designation.equals( "Administered by AFRINIC" ) )
            {
                value = "http://rdap.afrinic.net";
            }
            else if( record.designation.startsWith( "IANA" ) )
            {
                value = "http://rdap.iana.net";
            }
            else if( record.status.equals( "LEGACY" ) )
            {
                value = "http://rdap.arin.net";
            }
            else if( record.status.equals( "RESERVED" ) )
            {
                value = "http://rdap.iana.net";
            }
            allocations.put( key, value );
        }
    }

    public String getUrl( int prefix )
    {
        return allocations.get( prefix );
    }

    class Record
    {
        public String prefix;
        public String designation;
        public String status;
    }
}
