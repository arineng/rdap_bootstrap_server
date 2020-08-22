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

import net.arin.rdap_bootstrap.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Manages getting resource files.
 */
public class ResourceFiles
{
    public enum BootFiles
    {
        AS( "as_bootstrap" ),
        DOMAIN( "domain_bootstrap" ),
        V4( "v4_bootstrap" ),
        V6( "v6_bootstrap" ),
        ENTITY( "entity_bootstrap" );

        private final String key;

        public String getKey()
        {
            return key;
        }

        BootFiles( String key )
        {
            this.key = key;
        }
    }

    private final Properties resourceFiles;
    private final HashMap<String, Boolean> isFile;

    public ResourceFiles() throws IOException
    {
        String extFileName = System.getProperty( Constants.PROPERTY_PREFIX + "resource_files" );
        resourceFiles = new Properties();
        File file;
        if ( extFileName == null )
        {
            InputStream inputStream = getClass().getResourceAsStream( "/resource_files.properties" );
            resourceFiles.load( inputStream );
        }
        else if ( ( file = new File( extFileName ) ).isFile() )
        {
            InputStream inputStream = new FileInputStream( file );
            resourceFiles.load( inputStream );
        }
        // Override with explicitly set system properties.
        for ( BootFiles bootFiles : BootFiles.values() )
        {
            String value = System.getProperty( Constants.PROPERTY_PREFIX + "bootfile." + bootFiles.key );
            if ( value != null && value.length() > 0 )
            {
                resourceFiles.put( bootFiles.key, value );
            }
        }
        isFile = new HashMap<>();
        for ( Entry<Object, Object> entry : resourceFiles.entrySet() )
        {
            file = new File( entry.getValue().toString() );
            isFile.put( entry.getKey().toString(), file.exists() );
        }
    }

    public InputStream getInputStream( String key ) throws FileNotFoundException
    {
        if ( isFile.get( key ) )
        {
            File file = new File( resourceFiles.getProperty( key ) );
            return new FileInputStream( file );
        }
        // else
        return getClass().getResourceAsStream( resourceFiles.getProperty( key ) );
    }

    public long getLastModified( String key )
    {
        if ( !isFile.get( key ) )
        {
            return System.currentTimeMillis();
        }
        // else
        File file = new File( resourceFiles.getProperty( key ) );
        return file.lastModified();
    }
}
