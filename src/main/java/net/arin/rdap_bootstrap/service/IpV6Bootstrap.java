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

import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;
import net.arin.rdap_bootstrap.service.JsonBootstrapFile.ServiceUrls;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.Map;
import java.util.TreeMap;

/**
 * @version $Rev$, $Date$
 */
public class IpV6Bootstrap implements JsonBootstrapFile.Handler
{
    private volatile TreeMap<Long,ServiceUrls> allocations = new TreeMap<Long, ServiceUrls>(  );
    private TreeMap<Long,ServiceUrls> _allocations;

    private ServiceUrls serviceUrls;

    @Override
    public void startServices()
    {
        _allocations = new TreeMap<Long, ServiceUrls>(  );
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
        // Nothing to do
    }

    @Override
    public void addServiceEntry( String entry )
    {
        IPv6Network v6net = IPv6Network.fromString( entry );
        long key = v6net.getFirst().getHighBits();
        _allocations.put( key, serviceUrls );
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
        bsFile.loadData( resourceFiles.getInputStream( resourceFiles.V6_BOOTSTRAP ), this );
    }

    public ServiceUrls getServiceUrls( long prefix )
    {
        ServiceUrls retval = null;
        Map.Entry<Long,ServiceUrls> entry = allocations.floorEntry( prefix );
        if( entry != null )
        {
            retval = entry.getValue();
        }
        return retval;
    }

    public ServiceUrls getServiceUrls( IPv6Address addr )
    {
        return getServiceUrls( addr.getHighBits() );
    }

    public ServiceUrls getServiceUrls( IPv6Network net )
    {
        return getServiceUrls( net.getFirst().getHighBits() );
    }

}
