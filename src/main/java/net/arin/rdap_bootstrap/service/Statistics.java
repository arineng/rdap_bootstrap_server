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
 */
package net.arin.rdap_bootstrap.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @version $Rev$, $Date$
 */
public class Statistics
{
    private static class LruMap<String,AtomicLong> extends LinkedHashMap<String, AtomicLong >
    {
        private final int maxEntries;

        public LruMap( int maxEntries )
        {
            this.maxEntries = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry( Entry<String, AtomicLong> entry )
        {
            return super.size() > maxEntries;
        }
    }

    public enum UrlHits
    {
        IPHITS( "IP Hits" ),
        DOMAINHITS( "Domain Hits" ),
        ENTITYHITS( "Entity Hits" ),
        NAMESERVERHITS( "Nameserver Hits" ),
        DEFAULTHITS( "Default Hits" ),
        ASHITS( "Autnum Hits" );

        private Map<String,AtomicLong> hitsMap = Collections.synchronizedMap( new LruMap<String, AtomicLong>( 100 ) );
        private String title;

        public void hit( String url )
        {
            AtomicLong counter = hitsMap.get( url );
            if( counter == null )
            {
                hitsMap.put( url, new AtomicLong( 1 ) );
            }
            else
            {
                counter.incrementAndGet();
            }
        }

        public Set<Entry<String,AtomicLong>> getEntrySet()
        {
            return hitsMap.entrySet();
        }

        public String getTitle()
        {
            return title;
        }

        private UrlHits( String title )
        {
            this.title = title;
        }
    }

    private AtomicLong totalHits = new AtomicLong( 0 );
    private AtomicLong totalMisses = new AtomicLong( 0 );

    public AtomicLong getTotalHits()
    {
        return totalHits;
    }

    public AtomicLong getTotalMisses()
    {
        return totalMisses;
    }
}
