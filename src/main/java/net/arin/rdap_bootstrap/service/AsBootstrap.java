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

import net.arin.rdap_bootstrap.service.JsonBootstrapFile.ServiceUrls;
import net.arin.rdap_bootstrap.service.ResourceFiles.BootFiles;

import java.util.Map;
import java.util.TreeMap;

/**
 * @version $Rev$, $Date$
 */
public class AsBootstrap implements JsonBootstrapFile.Handler
{
    private class AsRangeInfo
    {
        private Long asStart;
        private Long asEnd;
        private ServiceUrls serviceUrls;

        public AsRangeInfo( Long asStart, Long asEnd,
                            ServiceUrls serviceUrls )
        {
            this.asStart = asStart;
            this.asEnd = asEnd;
            this.serviceUrls = serviceUrls;
        }

        public Long getAsStart()
        {
            return asStart;
        }

        public Long getAsEnd()
        {
            return asEnd;
        }

        public ServiceUrls getServiceUrls()
        {
            return serviceUrls;
        }
    }

    private volatile TreeMap<Long,AsRangeInfo> allocations = new TreeMap<Long, AsRangeInfo>(  );
    private TreeMap<Long,AsRangeInfo> _allocations;

    private JsonBootstrapFile.ServiceUrls serviceUrls;
    private String publication;

    @Override
    public void startServices()
    {
        _allocations = new TreeMap<Long, AsRangeInfo>();
    }

    @Override
    public void endServices()
    {
        allocations = _allocations;
    }

    @Override
    public void startService()
    {
        serviceUrls = new ServiceUrls();
    }

    @Override
    public void endService()
    {
        //nothing to do
    }

    @Override
    public void addServiceEntry( String entry )
    {
        if( entry != null )
        {
            String[] arr = entry.split("-");
            long key = Long.parseLong( arr[0] );
            if( !_allocations.containsKey( key ) )
            {
                long max = key;
                if( arr.length ==2 )
                {
                    max = Long.parseLong( arr[1] );
                }
                AsRangeInfo asRangeInfo = new AsRangeInfo( key, max, serviceUrls );
                _allocations.put( key, asRangeInfo );
            }
        }
    }

    @Override
    public void addServiceUrl( String url )
    {
        serviceUrls.addUrl( url );
    }

    public void loadData( ResourceFiles resourceFiles )
        throws Exception
    {
        JsonBootstrapFile bsFile = new JsonBootstrapFile();
        bsFile.loadData( resourceFiles.getInputStream( BootFiles.AS.getKey() ), this );
    }

    public ServiceUrls getServiceUrls( String autnum )
    {
        long number = Long.parseLong( autnum );
        Map.Entry<Long,AsRangeInfo> entry = allocations.floorEntry( number );
        if( entry != null )
        {
            AsRangeInfo asRangeInfo = entry.getValue();
            if( number <= asRangeInfo.getAsEnd() )
            {
                return asRangeInfo.getServiceUrls();
            }
        }
        //else
        return null;
    }

    @Override
    public void setPublication( String publication ) { this.publication = publication; }
    public String getPublication() { return publication; }

}
