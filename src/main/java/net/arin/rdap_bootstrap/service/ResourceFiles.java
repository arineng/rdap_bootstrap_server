/*
 * Copyright (C) 2013, 2014 American Registry for Internet Numbers (ARIN)
 */
package net.arin.rdap_bootstrap.service;

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
 * @version $Rev$, $Date$
 */
public class ResourceFiles
{
    public enum BootFiles
    {
        DEFAULT( "default_bootstrap" ),
        AS( "as_bootstrap" ),
        DOMAIN( "domain_bootstrap" ),
        V4( "v4_bootstrap" ),
        V6( "v6_bootstrap" ),
        ENTITY( "entity_bootstrap" );

        private String key;

        public String getKey()
        {
            return key;
        }

        private BootFiles( String key )
        {
            this.key = key;
        }
    }

    public final static String PROPERTY_PREFIX = "arin.rdapbootstrap.";

    private Properties resourceFiles;
    private HashMap<String,Boolean> isFile;

    public ResourceFiles() throws IOException
    {
        String extFileName = System.getProperty( PROPERTY_PREFIX + "resource_files" );
        resourceFiles = new Properties(  );
        File file;
        if( extFileName == null )
        {
            InputStream inputStream = getClass().getResourceAsStream( "/resource_files.properties" );
            resourceFiles.load( inputStream );
        }
        else if( ( file = new File( extFileName ) ).isFile() )
        {
            InputStream inputStream = new FileInputStream( file );
            resourceFiles.load( inputStream );
        }
        else
        {
            for ( BootFiles bootFiles : BootFiles.values() )
            {
               resourceFiles.put( bootFiles.key, System.getProperty( PROPERTY_PREFIX + "bootfile." + bootFiles.key ) );
            }
        }
        isFile = new HashMap<String, Boolean>(  );
        for ( Entry<Object, Object> entry : resourceFiles.entrySet() )
        {
            file = new File( entry.getValue().toString() );
            isFile.put( entry.getKey().toString(), file.exists() );
        }
    }

    public InputStream getInputStream( String key ) throws FileNotFoundException
    {
        if( isFile.get( key ) )
        {
            File file = new File( resourceFiles.getProperty( key ) );
            return new FileInputStream( file );
        }
        //else
        return getClass().getResourceAsStream( resourceFiles.getProperty( key ) );
    }

    public long getLastModified( String key )
    {
        if( !isFile.get( key ) )
        {
            return System.currentTimeMillis();
        }
        //else
        File file = new File( resourceFiles.getProperty( key ) );
        return file.lastModified();
    }
}
