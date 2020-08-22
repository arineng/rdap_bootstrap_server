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
 *
 */
package net.arin.rdap_bootstrap.service;

import java.util.Map;
import java.util.TreeMap;

import net.arin.rdap_bootstrap.service.JsonBootstrapFile.ServiceUrls;
import net.arin.rdap_bootstrap.service.ResourceFiles.BootFiles;

import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;

public class IpV6Bootstrap implements JsonBootstrapFile.Handler
{
    private static class HighBitsRangeInfo
    {
        private final Long highBitsStart;
        private final Long highBitsEnd;
        private final ServiceUrls serviceUrls;

        public HighBitsRangeInfo( Long highBitsStart, Long highBitsEnd, ServiceUrls serviceUrls )
        {
            this.highBitsStart = highBitsStart;
            this.highBitsEnd = highBitsEnd;
            this.serviceUrls = serviceUrls;
        }

        public Long getHighBitsStart()
        {
            return highBitsStart;
        }

        public Long getHighBitsEnd()
        {
            return highBitsEnd;
        }

        public ServiceUrls getServiceUrls()
        {
            return serviceUrls;
        }
    }

    private volatile TreeMap<Long, HighBitsRangeInfo> allocations = new TreeMap<>();
    private TreeMap<Long, HighBitsRangeInfo> _allocations;

    private ServiceUrls serviceUrls;
    private String publication;
    private String description;

    @Override
    public void startServices()
    {
        _allocations = new TreeMap<>();
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
        // Nothing to do.
    }

    @Override
    public void addServiceEntry( String entry )
    {
        IPv6Network v6net = IPv6Network.fromString( entry );
        long key = v6net.getFirst().getHighBits();
        int prefixLength = v6net.getNetmask().asPrefixLength();
        _allocations.put( key, new HighBitsRangeInfo( key, key + ( long ) ( Math.pow( 2, 64 - prefixLength ) - 1 ), serviceUrls ) );
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
        bsFile.loadData( resourceFiles.getInputStream( BootFiles.V6.getKey() ), this );
    }

    public ServiceUrls getServiceUrls( long prefixStart, long prefixEnd )
    {
        ServiceUrls retval = null;
        Map.Entry<Long, HighBitsRangeInfo> entry = allocations.floorEntry( prefixStart );
        if ( entry != null )
        {
            HighBitsRangeInfo highBitsRangeInfo = entry.getValue();
            if ( highBitsRangeInfo.getHighBitsStart() <= prefixStart && prefixEnd <= highBitsRangeInfo.getHighBitsEnd() )
            {
                retval = highBitsRangeInfo.getServiceUrls();
            }
        }
        return retval;
    }

    public ServiceUrls getServiceUrls( IPv6Address addr )
    {
        return getServiceUrls( addr.getHighBits(), addr.getHighBits() );
    }

    public ServiceUrls getServiceUrls( IPv6Network net )
    {
        long prefixStart = net.getFirst().getHighBits();
        int prefixLength = net.getNetmask().asPrefixLength();
        long prefixEnd = prefixStart + ( long ) ( Math.pow( 2, 64 - prefixLength ) - 1 );
        return getServiceUrls( prefixStart, prefixEnd );
    }

    @Override
    public void setPublication( String publication )
    {
        this.publication = publication;
    }

    public String getPublication()
    {
        return publication;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public void setDescription( String description )
    {
        this.description = description;
    }
}
