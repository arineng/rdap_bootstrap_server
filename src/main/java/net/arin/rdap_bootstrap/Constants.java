/*
 * Copyright (C) 2015-2020 American Registry for Internet Numbers (ARIN)
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
package net.arin.rdap_bootstrap;

/**
 * Holds app-wide constants.
 */
public class Constants
{
    public final static String PROPERTY_PREFIX = "arin.rdapbootstrap.";

    public final static String MATCH_SCHEME_ON_REDIRECT_PROPERTY = "arin.rdapbootstrap.match_scheme_on_redirect";
    public final static String DOWNLOAD_BOOTSTRAP_FILES_PROPERTY = "arin.rdapbootstrap.download_bootstrap_files";
    public final static String DOWNLOAD_ASN_FILE_URL_PROPERTY = "arin.rdapbootstrap.download_asn_file_url";
    public final static String DOWNLOAD_DOMAIN_FILE_URL_PROPERTY = "arin.rdapbootstrap.download_domain_file_url";
    public final static String DOWNLOAD_IPV4_FILE_URL_PROPERTY = "arin.rdapbootstrap.download_ipv4_file_url";
    public final static String DOWNLOAD_IPV6_FILE_URL_PROPERTY = "arin.rdapbootstrap.download_ipv6_file_url";
    public final static String DOWNLOAD_DIRECTORY_PROPERTY = "arin.rdapbootstrap.download_directory";
    public final static String DOWNLOAD_INTERVAL_PROPERTY = "arin.rdapbootstrap.download_interval";
    public final static String DOWNLOAD_MAX_ATTEMPTS_PROPERTY = "arin.rdapbootstrap.download_max_attempts";
}
