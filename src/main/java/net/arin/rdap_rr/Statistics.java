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
 */
package net.arin.rdap_rr;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @version $Rev$, $Date$
 */
public class Statistics
{
    private HashMap<String,AtomicLong> httpHits = new HashMap<String, AtomicLong>(  );
    private HashMap<String,AtomicLong> asRirHits = new HashMap<String, AtomicLong>(  );
    private HashMap<String,AtomicLong> ip4RirHits = new HashMap<String, AtomicLong>(  );
    private HashMap<String,AtomicLong> ip6RirHits = new HashMap<String, AtomicLong>(  );
    private HashMap<String,AtomicLong> entityRirHits = new HashMap<String, AtomicLong>(  );
    private HashMap<String,AtomicLong> domainRirHits = new HashMap<String, AtomicLong>(  );
    private HashMap<String,AtomicLong> domainTldHits = new HashMap<String, AtomicLong>(  );
    private HashMap<String,AtomicLong> nsTldHits = new HashMap<String, AtomicLong>(  );

    private RirMap rirMap = new RirMap();

    public Statistics() throws Exception
    {
        rirMap.loadData();
    }

    public void asHitByUrl( String url )
    {
        String rir = rirMap.getRirFromUrl( url );
        if( url != null )
        {
            AtomicLong hits = asRirHits.get( rir );
            if( rir != null )
            {
                hits.incrementAndGet();
            }
        }
    }

    public void addAsRirCounter( String rir )
    {
        if( !asRirHits.containsKey( rir ) )
        {
            asRirHits.put( rir, new AtomicLong( 0 ) );
        }
    }
}
