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
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @version $Rev$, $Date$
 */
public class DomainBootstrap implements JsonBootstrapFile.Handler
{
    private volatile HashMap<String,ServiceUrls> allocations = new HashMap<String, ServiceUrls>(  );
    private HashMap<String,ServiceUrls> _allocations;

    private ServiceUrls serviceUrls;
    private String publication;

    public void loadData( ResourceFiles resourceFiles )
        throws Exception
    {
        JsonBootstrapFile bsFile = new JsonBootstrapFile();
        bsFile.loadData( resourceFiles.getInputStream( BootFiles.DOMAIN.getKey() ), this );
    }

    @Override
    public void startServices()
    {
        _allocations = new HashMap<String, ServiceUrls>(  );
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
        //Nothing to do
    }

    @Override
    public void addServiceEntry( String entry )
    {
        _allocations.put( entry.toUpperCase(), serviceUrls );
    }

    @Override
    public void addServiceUrl( String url )
    {
        serviceUrls.addUrl( url );
    }

    public ServiceUrls getServiceUrls( String domain )
    {
        domain = domain.toUpperCase();
        int idx = 0;
        ServiceUrls retval = null;
        while( idx != -1 )
        {
            retval = allocations.get( domain.substring( idx ) );
            if( retval != null )
            {
                break;
            }
            //else
            idx = domain.indexOf( ".", idx );
            if( idx != -1 )
            {
                idx++;
            }
        }
        return retval;
    }

    @Override
    public void setPublication( String publication ) { this.publication = publication; }
    public String getPublication() { return publication; }

}
