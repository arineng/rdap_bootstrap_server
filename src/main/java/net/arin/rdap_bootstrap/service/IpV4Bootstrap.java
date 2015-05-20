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

import java.util.HashMap;

/**
 * @version $Rev$, $Date$
 */
public class IpV4Bootstrap implements JsonBootstrapFile.Handler
{
    private volatile HashMap<Integer,ServiceUrls> allocations = new HashMap<Integer, ServiceUrls>(  );
    private HashMap<Integer,ServiceUrls> _allocations;

    private ServiceUrls serviceUrls;
    private String publication;

    public void loadData( ResourceFiles resourceFiles )
        throws Exception
    {
        JsonBootstrapFile bsFile = new JsonBootstrapFile();
        bsFile.loadData( resourceFiles.getInputStream( BootFiles.V4.getKey() ), this );
    }

    @Override
    public void startServices()
    {
        _allocations = new HashMap<Integer, ServiceUrls>(  );
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
        int key = Integer.parseInt( entry.split( "/" )[0].split( "\\." )[0] );
        _allocations.put( key, serviceUrls );
    }

    @Override
    public void addServiceUrl( String url )
    {
        serviceUrls.addUrl( url );
    }

    public ServiceUrls getServiceUrls( int prefix )
    {
        return allocations.get( prefix );
    }

    @Override
    public void setPublication( String publication ) { this.publication = publication; }
    public String getPublication() { return publication; }
}
