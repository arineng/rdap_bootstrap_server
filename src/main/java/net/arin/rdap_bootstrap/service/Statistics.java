/*
 * Copyright (C) 2013-2020 American Registry for Internet Numbers (ARIN)
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class Statistics
{
    private static class LruMap<String, AtomicLong> extends LinkedHashMap<String, AtomicLong>
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
        DOMAINHITS( "Domain Hits" ),
        NAMESERVERHITS( "Nameserver Hits" ),
        IPHITS( "IP Hits" ),
        ASHITS( "Autnum Hits" ),
        ENTITYHITS( "Entity Hits" );

        private final Map<String, AtomicLong> hitsMap = Collections.synchronizedMap( new LruMap<>( 100 ) );
        private final String title;

        public void hit( String url )
        {
            AtomicLong counter = hitsMap.get( url );
            if ( counter == null )
            {
                hitsMap.put( url, new AtomicLong( 1 ) );
            }
            else
            {
                counter.incrementAndGet();
            }
        }

        public Set<Entry<String, AtomicLong>> getEntrySet()
        {
            return hitsMap.entrySet();
        }

        public String getTitle()
        {
            return title;
        }

        UrlHits( String title )
        {
            this.title = title;
        }
    }

    private final AtomicLong totalHits = new AtomicLong( 0 );
    private final AtomicLong totalMisses = new AtomicLong( 0 );

    public AtomicLong getTotalHits()
    {
        return totalHits;
    }

    public AtomicLong getTotalMisses()
    {
        return totalMisses;
    }
}
