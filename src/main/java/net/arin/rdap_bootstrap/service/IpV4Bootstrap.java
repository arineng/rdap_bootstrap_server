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

import java.util.HashMap;
import java.util.Set;

import net.arin.rdap_bootstrap.service.JsonBootstrapFile.ServiceUrls;
import net.arin.rdap_bootstrap.service.ResourceFiles.BootFiles;
import net.ripe.ipresource.IpRange;
import net.ripe.ipresource.UniqueIpResource;

/**
 * @version $Rev$, $Date$
 */
public class IpV4Bootstrap implements JsonBootstrapFile.Handler {
	private volatile HashMap<String, ServiceUrls> allocations = new HashMap<String, ServiceUrls>();
	private HashMap<String, ServiceUrls> _allocations; 

	private ServiceUrls serviceUrls;
	private String publication;
	private String description;

	public void loadData(ResourceFiles resourceFiles) throws Exception {
		JsonBootstrapFile bsFile = new JsonBootstrapFile();
		bsFile.loadData(resourceFiles.getInputStream(BootFiles.V4.getKey()), this);
	}

	@Override
	public void startServices() {
		_allocations = new HashMap<String, ServiceUrls>();
	}

	@Override
	public void endServices() {
		allocations = _allocations;
	}

	@Override
	public void startService() {
		serviceUrls = new ServiceUrls();
	}

	@Override
	public void endService() {
		// nothing to do
	}

	@Override
	public void addServiceEntry(String entry) {
		_allocations.put(entry, serviceUrls);
	}

	@Override
	public void addServiceUrl(String url) {
		serviceUrls.addUrl(url);
	}

	public ServiceUrls getServiceUrls(String prefix) {
		
		UniqueIpResource start;
		try {
			// /8 single int behaviour
			new Integer(prefix);
			start = IpRange.parse(prefix + ".0.0.0/8").getStart();
		} catch (NumberFormatException e) {
			start = IpRange.parse(prefix).getStart();
		}
		
		ServiceUrls resultUrl = null;
		IpRange resultNetwork = IpRange.parse("0.0.0.0/0");
		final Set<String> keys = allocations.keySet();
		for (String key : keys) {
			final IpRange network = IpRange.parse(key);
			if(network.contains(start) && (resultNetwork.getPrefixLength() < network.getPrefixLength())) {
				resultNetwork = network;
				resultUrl = allocations.get(key);
			}
		}
		return resultUrl;
	}

	@Override
	public void setPublication(String publication) {
		this.publication = publication;
	}

	public String getPublication() {
		return publication;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}
}
