/*
 * Copyright (C) 2013-2015 American Registry for Internet Numbers (ARIN)
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
public class DefaultBootstrap implements JsonBootstrapFile.Handler
{
    public enum Type {
        NAMESERVER("nameserver"),
        IP("ip"),
        AUTNUM("autnum"),
        ENTITY("entity"),
        DOMAIN("domain");

        private String pValue;
        private Type( String pValue )
        {
            this.pValue = pValue;
        }

        public String getPValue()
        {
            return pValue;
        }
    }

    private volatile HashMap<String,ServiceUrls> allocations = new HashMap<String, ServiceUrls>(  );
    private HashMap<String,ServiceUrls> _allocations;

    private ServiceUrls serviceUrls;
    private String publication;

    public void loadData( ResourceFiles resourceFiles )
        throws Exception
    {
        JsonBootstrapFile bsFile = new JsonBootstrapFile();
        bsFile.loadData( resourceFiles.getInputStream( BootFiles.DEFAULT.getKey() ), this );
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
        _allocations.put( entry, serviceUrls );
    }

    @Override
    public void addServiceUrl( String url )
    {
        serviceUrls.addUrl( url );
    }

    public ServiceUrls getServiceUrls( Type type )
    {
        return allocations.get( type.getPValue() );
    }

    @Override
    public void setPublication( String publication ) { this.publication = publication; }
    public String getPublication() { return publication; }

}
