/*
 * Copyright (C) 2020 American Registry for Internet Numbers (ARIN)
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
package net.arin.rdap_bootstrap.spring;

import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;

import java.io.File;
import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AppProperties
{
    protected AppProperties()
    {
    }

    public static final String PLACE_VALUE_HERE = "***PLACE_VALUE_HERE***";

    private static final boolean allowForDefaultValues = true;

    private static PropertyResolver resolver = new StandardEnvironment();

    public static void updateResolver( PropertyResolver resolver )
    {
        if ( resolver == null )
        {
            throw new IllegalArgumentException( "Resolver cannot be null" );
        }

        AppProperties.resolver = resolver;
    }

    public static String getProperty( String name )
    {
        return resolver.containsProperty( name ) ? resolver.getProperty( name ) : null;
    }

    public static String getProperty( String name, String defaultValue )
    {
        return resolver.containsProperty( name ) ? resolver.getProperty( name ) : defaultValue;
    }

    public static void bind( String name, Object value )
    {
        System.setProperty( name, value.toString() );
    }

    public static void unbind( String name )
    {
        System.clearProperty( name );
    }

    public static Integer lookupInteger( String name )
    {
        return lookup( Integer.class, name );
    }

    public static Long lookupLong( String name )
    {
        return lookup( Long.class, name );
    }

    public static Long lookupLong( String name, long defaultValue )
    {
        return lookup( Long.class, name, defaultValue );
    }

    public static BigDecimal lookupBigDecimal( String name )
    {
        return lookup( BigDecimal.class, name );
    }

    public static String lookupString( String name )
    {
        return lookup( String.class, name );
    }

    public static Boolean lookupBoolean( String name )
    {
        return lookup( Boolean.class, name );
    }

    public static Integer lookupInteger( String name, int defaultValue )
    {
        return lookup( Integer.class, name, defaultValue );
    }

    public static String lookupString( String name, String defaultValue )
    {
        return lookup( String.class, name, defaultValue );
    }

    public static String lookupDirectory( String name )
    {
        String directory = lookupString( name );
        return directory.endsWith( File.separator ) ? directory : directory + File.separator;
    }

    public static Boolean lookupBoolean( String name, Boolean defaultValue )
    {
        return lookup( Boolean.class, name, defaultValue );
    }

    public static <T> T lookup( Class<T> clazz, String name )
    {
        String value = getProperty( name, false, allowForDefaultValues );
        return parseValue( value, clazz, name );
    }

    public static <T> T lookup( Class<T> clazz, String name, T defaultValue )
    {
        String value = getProperty( name, true, allowForDefaultValues );
        if ( value == null )
        {
            return defaultValue;
        }

        return parseValue( value, clazz, name );
    }

    public static <T> T lookupForceAllowDefault( Class<T> clazz, String name, T defaultValue )
    {
        String value = getProperty( name, true, true );
        if ( value == null )
        {
            return defaultValue;
        }

        return parseValue( value, clazz, name );
    }

    private static String getProperty( String name, boolean nullable, boolean allowForDefaultValues )
    {
        String value = getProperty( name );

        if ( value == null )
        {
            if ( !nullable )
            {
                throw new RuntimeException( "System property '" + name + "' not found." );
            }
            if ( !allowForDefaultValues )
            {
                throw new RuntimeException( "System property '" + name + "' not found and default values are not allowed." );
            }
        }
        else if ( value.equalsIgnoreCase( PLACE_VALUE_HERE ) )
        {
            if ( !nullable )
            {
                throw new RuntimeException( "System property '" + name + "' is '" + PLACE_VALUE_HERE + "' (i.e., not set)" );
            }
            if ( !allowForDefaultValues )
            {
                throw new RuntimeException( "System property '" + name + "' is '" + PLACE_VALUE_HERE + "' (i.e., not set) and default values are not allowed." );
            }
        }

        return value;
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T parseValue( String value, Class<T> clazz, String name )
    {
        if ( clazz == String.class )
        {
            return ( T ) value;
        }
        else if ( clazz == Boolean.class )
        {
            if ( value.equalsIgnoreCase( Boolean.TRUE.toString() ) )
            {
                return ( T ) Boolean.TRUE;
            }
            else if ( value.equalsIgnoreCase( Boolean.FALSE.toString() ) )
            {
                return ( T ) Boolean.FALSE;
            }
            else
            {
                throw new RuntimeException( "System property '" + name + "' is not a boolean value" );
            }
        }
        else if ( clazz == Integer.class )
        {
            return ( T ) Integer.decode( value );
        }
        else if ( clazz == Long.class )
        {
            return ( T ) Long.decode( removeEndingL( value ) );
        }
        else if ( clazz == BigDecimal.class )
        {
            return ( T ) new BigDecimal( removeEndingL( value ) );
        }
        else
        {
            throw new UnsupportedOperationException( "System property of " + clazz + " type is not supported" );
        }
    }

    private static String removeEndingL( String value )
    {
        String newValue = value.trim();
        return newValue.endsWith( "L" ) ? newValue.substring( 0, newValue.length() - 1 ) : newValue;
    }

    public static SortedMap<String, String> getSystemProperties( String prefix )
    {
        return System.getProperties().keySet().stream()
                .map( Object::toString )
                .filter( name -> name.startsWith( prefix ) )
                .collect( Collectors.toMap( Function.identity(), AppProperties::getProperty, ( v1, v2 ) -> v1, TreeMap::new ) );
    }
}
