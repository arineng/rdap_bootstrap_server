/*
 * Copyright (C) 2013 American Registry for Internet Numbers (ARIN)
 */
package net.arin.rdap_rr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

/**
 * @version $Rev$, $Date$
 */
public class TldAllocations
{
    private HashMap<String,String> allocations = new HashMap<String, String>(  );

    public void loadData()
        throws Exception
    {
        InputStream inputStream = getClass().getResourceAsStream( "/tlds-alpha-by-domain.txt" );
        BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
        String line = null;
        while( (line = reader.readLine()) != null )
        {
            String key = line.trim();
            String value = "://rdap." + key;
            allocations.put( key, value );
        }
    }


    public String getUrl( String tld )
    {
        return allocations.get( tld.toUpperCase() );
    }

}
