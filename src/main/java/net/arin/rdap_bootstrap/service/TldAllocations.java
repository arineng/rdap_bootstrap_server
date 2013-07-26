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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @version $Rev$, $Date$
 */
public class TldAllocations
{
    private HashMap<String,String> allocations = new HashMap<String, String>(  );

    public void loadData( ResourceFiles resourceFiles )
        throws Exception
    {
        Properties props = new Properties(  );
        props.load( resourceFiles.getInputStream( ResourceFiles.TLD_MAP ) );
        for( Entry<Object, Object> entry: props.entrySet() )
        {
            String key = entry.getKey().toString().trim();
            String value = entry.getValue().toString().trim();
            allocations.put( key.toUpperCase(), value );
        }
    }

    public String getUrl( String tld )
    {
        return getUrl( tld, null );
    }

    public String getUrl( String tld, HitCounter hitCounter )
    {
        tld = tld.toUpperCase();
        String retval = allocations.get( tld );
        if( hitCounter != null )
        {
            hitCounter.incrementCounter( tld );
        }
        return retval;
    }

    public void addDomainTldCountersToStatistics( Statistics stats )
    {
        for ( String s : allocations.keySet() )
        {
            stats.addDomainTldCounter( s );
        }
    }

    public void addNsTldCountersToStatistics( Statistics stats )
    {
        for ( String s : allocations.keySet() )
        {
            stats.addNsTldCounter( s );
        }
    }

    public void addEntityTldCountersToStatistics( Statistics stats )
    {
        for ( String s : allocations.keySet() )
        {
            stats.addEntityTldCounter( s );
        }
    }
}
