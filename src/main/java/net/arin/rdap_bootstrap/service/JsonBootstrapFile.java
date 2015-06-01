/*
 * Copyright (C) 2014-2015 American Registry for Internet Numbers (ARIN)
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * An abstraction for generically processing the JSON bootstrap files.
 * @version $Rev$, $Date$
 */
public class JsonBootstrapFile
{

    /**
     * The various versions of the bootstrap are expected to pass in an
     * implementation of this handler.
     */
    public interface Handler
    {
        public void startServices();
        public void endServices();
        public void startService();
        public void endService();
        public void addServiceEntry( String entry );
        public void addServiceUrl( String url );
        public void setPublication( String publication );
    }

    /**
     * A utility class for referencing URLs.
     */
    public static class ServiceUrls
    {
        private ArrayList<String> urls = new ArrayList<String>();
        private int httpIdx = -1;
        private int httpsIdx = -1;

        public void addUrl( String url )
        {
            if( url != null )
            {
                if( url.endsWith( "/" ) )
                {
                    url = url.substring( 0, url.length() - 1 );
                }
                urls.add( url );
                if( url.startsWith( "http://" ) )
                {
                    httpIdx = urls.size() -1;
                }
                else if( url.startsWith( "https://" ) )
                {
                    httpsIdx = urls.size() - 1;
                }
            }
        }

        public ArrayList<String> getUrls()
        {
            return urls;
        }

        public void setUrls( ArrayList<String> urls )
        {
            this.urls = urls;
        }

        public String getHttpUrl()
        {
            if( httpIdx != -1 ){
                return urls.get( httpIdx );
            }
            //else
            return null;
        }

        public String getHttpsUrl()
        {
            if( httpsIdx != -1 ){
                return urls.get( httpsIdx );
            }
            //else
            return null;
        }
    }

    public void loadData( InputStream inputStream, Handler handler )
        throws Exception
    {
        JsonFactory jsonFactory = new JsonFactory(  );
        JsonParser jsonParser = jsonFactory.createParser( inputStream );

        if( jsonParser.nextToken() != JsonToken.START_OBJECT )
        {
            throw new RuntimeException( "JSON file does not start with a JSON object" );
        }
        //else
        while( jsonParser.nextToken() != JsonToken.END_OBJECT )
        {
            if( jsonParser.getCurrentToken() == JsonToken.FIELD_NAME &&
                jsonParser.getCurrentName().equals( "rdap_bootstrap" ) )
            {
                if( jsonParser.nextToken() != JsonToken.START_OBJECT )
                {
                    throw new RuntimeException( "'rdap_bootstrap' is not an ojbect." );
                }
                //else
                while( jsonParser.nextToken() != JsonToken.END_OBJECT )
                {
                    if( jsonParser.getCurrentToken() == JsonToken.FIELD_NAME &&
                        jsonParser.getCurrentName().equals( "version" ) )
                    {
                        if( jsonParser.nextToken() != JsonToken.VALUE_STRING )
                        {
                            throw new RuntimeException( "'version' is not a string" );
                        }
                        //else
                        String version = jsonParser.getValueAsString();
                        if( version == null || !version.equals( "1.0" ) )
                        {
                            throw new RuntimeException( "'version' is not '1.0'" );
                        }
                    }
                    else if( jsonParser.getCurrentToken() == JsonToken.FIELD_NAME &&
                            jsonParser.getCurrentName().equals( "publication" ) )
                    {
                        // These are dates, but since we're outputting them as Strings anyway, and not really doing
                        // anything else with them leaving them as Strings should be okay.
                        if( jsonParser.nextToken() != JsonToken.VALUE_STRING )
                        {
                            throw new RuntimeException( "'publication' is not a string" );
                        }

                        handler.setPublication( jsonParser.getValueAsString() );
                    }
                    else if( jsonParser.getCurrentToken() == JsonToken.FIELD_NAME &&
                        jsonParser.getCurrentName().equals( "services" ) )
                    {
                        if( jsonParser.nextToken() != JsonToken.START_ARRAY )
                        {
                            throw new RuntimeException( "'services' is not an array" );
                        }
                        //else
                        handler.startServices();
                        while( jsonParser.nextToken() != JsonToken.END_ARRAY )
                        {
                            if( jsonParser.getCurrentToken() != JsonToken.START_ARRAY )
                            {
                                throw new RuntimeException( "expected array at " + jsonParser.getCurrentLocation() );
                            }
                            //else
                            handler.startService();
                            while( jsonParser.nextToken() != JsonToken.END_ARRAY )
                            {
                                if( jsonParser.getCurrentToken() != JsonToken.START_ARRAY )
                                {
                                    throw new RuntimeException( "expected array at " + jsonParser.getCurrentLocation() );
                                }
                                //else
                                while( jsonParser.nextToken() != JsonToken.END_ARRAY )
                                {
                                    if( jsonParser.getCurrentToken() != JsonToken.VALUE_STRING )
                                    {
                                        throw new RuntimeException( "service entry at " +
                                            jsonParser.getCurrentLocation() + " is not a string" );
                                    }
                                    //else
                                    handler.addServiceEntry( jsonParser.getValueAsString() );
                                }
                                if( jsonParser.nextToken() != JsonToken.START_ARRAY )
                                {
                                    throw new RuntimeException( "expected array at " + jsonParser.getCurrentLocation() );
                                }
                                //else
                                while ( jsonParser.nextToken() != JsonToken.END_ARRAY )
                                {
                                    if ( jsonParser.getCurrentToken() != JsonToken.VALUE_STRING )
                                    {
                                        throw new RuntimeException( "service URL at " +
                                            jsonParser.getCurrentLocation() + " is not a string" );
                                    }
                                    //else
                                    handler.addServiceUrl( jsonParser.getValueAsString() );
                                }
                            }
                            handler.endService();
                        }
                        handler.endServices();
                    }
                }
            }
        }
    }

}
