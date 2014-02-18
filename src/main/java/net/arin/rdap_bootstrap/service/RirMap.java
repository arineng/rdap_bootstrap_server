/*
 * Copyright (C) 2013, 2014 American Registry for Internet Numbers (ARIN)
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
public class RirMap
{
    private volatile Properties rirMap = new Properties(  );
    private volatile HashMap<String,String> reverseMap = new HashMap<String, String>(  );

    public void loadData( ResourceFiles resourceFiles )
        throws Exception
    {
        InputStream inputStream = resourceFiles.getInputStream( ResourceFiles.RIR_MAP );
        Properties _rirMap = new Properties(  );
        _rirMap.load( inputStream );
        HashMap<String,String> _reverseMap = new HashMap<String, String>(  );
        for ( Entry<Object, Object> entry : _rirMap.entrySet() )
        {
            _reverseMap.put( (String)entry.getValue(), (String)entry.getKey() );
        }
        rirMap = _rirMap;
        reverseMap = _reverseMap;
    }

    public String getRirUrl( String rir )
    {
        return rirMap.getProperty( rir );
    }

    public String getRirFromUrl( String url )
    {
        return reverseMap.get( url );
    }

    public void addEntityRirCountersToStatistics( Statistics stats )
    {
        for ( Object s : rirMap.keySet() )
        {
            stats.addEntityRirCounter( s.toString() );
        }
    }
}
