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
    public final static String AS_ALLOCATIONS = "as_allocations";
    public final static String RIR_MAP        = "rir_map";
    public final static String TLD_MAP        = "tld_map";
    public final static String V4_ALLOCATIONS = "v4_allocations";
    public final static String V6_ALLOCATIONS = "v6_allocations";

    private Properties resourceFiles;
    private HashMap<String,Boolean> isFile;

    public ResourceFiles() throws IOException
    {
        String extFileName = System.getProperty( "arin.rdapbootstrap.resource_files" );
        resourceFiles = new Properties(  );
        if( extFileName == null )
        {
            InputStream inputStream = getClass().getResourceAsStream( "/resource_files.properties" );
            resourceFiles.load( inputStream );
        }
        else
        {
            File file = new File( extFileName );
            InputStream inputStream = new FileInputStream( file );
            resourceFiles.load( inputStream );
        }
        isFile = new HashMap<String, Boolean>(  );
        for ( Entry<Object, Object> entry : resourceFiles.entrySet() )
        {
            File file = new File( entry.getValue().toString() );
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
