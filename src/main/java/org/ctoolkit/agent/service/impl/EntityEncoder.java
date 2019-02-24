/*
 * Copyright (c) 2017 Comvai, s.r.o. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.ctoolkit.agent.service.impl;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.repackaged.com.google.api.client.util.Base64;
import org.ctoolkit.agent.resource.ChangeSetEntityProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Datastore entity encoder
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class EntityEncoder
{
    private static final Logger logger = LoggerFactory.getLogger( EntityEncoder.class );

    /**
     * Transforms a type and a string value to real object with given type and value.
     *
     * @param type         the type of the property
     * @param multiplicity multiplicity of property
     * @param value        the string represented value of the property
     * @return ChangeSetEntityProperty representation of the property
     */
    public Object decodeProperty( String type, String multiplicity, String value )
    {
        if ( value == null )
        {
            return null;
        }
        if ( ChangeSetEntityProperty.PROPERTY_TYPE_STRING.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Object toValue( String value )
                {
                    // if string is bigger than 1500 bytes create Text object instead
                    if ( value.getBytes().length > 1500 )
                    {
                        return new Text( value );
                    }
                    return value;
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_DOUBLE.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Object toValue( String value )
                {
                    return Double.valueOf( value );
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_LONG.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Object toValue( String value )
                {
                    return Long.valueOf( value );
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_BOOLEAN.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Object toValue( String value )
                {
                    return Boolean.valueOf( value );
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_DATE.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Object toValue( String value )
                {
                    return new Date( Long.valueOf( value ) );
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_BLOB.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Object toValue( String value )
                {
                    try
                    {
                        return new Blob( Base64.decodeBase64( value ) );
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Error by encoding blob: '" + value + "'" );
                        return null;
                    }
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_REFERENCE.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Object toValue( String value )
                {
                    return parseKeyByIdOrName( value );
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_REFERENCE_NAME.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Object toValue( String value )
                {
                    return parseKeyByName( value );
                }
            }.resolve( value, multiplicity );
        }

        logger.error( "Unknown entity type '" + type + "'" );
        return null;
    }

    /**
     * Parse given string to Key first try as Long Id then if parsing fails as String name.
     *
     * @param stringKey the input string key to parse
     * @return the parsed key
     */
    public Key parseKeyByIdOrName( String stringKey )
    {
        String[] split = stringKey.trim().split( "::" );

        String kind;
        String idName;
        Key parentKey = null;

        for ( String s : split )
        {
            String[] spl = s.split( ":" );
            kind = spl[0].trim();
            idName = spl[1].trim();

            if ( parentKey == null )
            {
                try
                {
                    parentKey = KeyFactory.createKey( kind, Long.parseLong( idName ) );
                }
                catch ( NumberFormatException e )
                {
                    parentKey = KeyFactory.createKey( kind, idName );
                }
            }
            else
            {
                try
                {
                    parentKey = KeyFactory.createKey( parentKey, kind, Long.parseLong( idName ) );
                }
                catch ( NumberFormatException e )
                {
                    parentKey = KeyFactory.createKey( parentKey, kind, idName );
                }
            }
        }

        return parentKey;
    }

    /**
     * Parse given string to Key first try as Long Id then if parsing fails as String name.
     *
     * @param stringKey the input string key to parse
     * @return the parsed key
     */
    public Key parseKeyByName( String stringKey )
    {
        String[] split = stringKey.trim().split( "::" );

        String kind;
        String name;
        Key parentKey = null;

        for ( String s : split )
        {
            String[] spl = s.split( ":" );
            kind = spl[0].trim();
            name = spl[1].trim();

            if ( parentKey == null )
            {
                parentKey = KeyFactory.createKey( kind, name );
            }
            else
            {
                parentKey = KeyFactory.createKey( parentKey, kind, name );
            }
        }

        return parentKey;
    }

    private static abstract class ValueResolver
    {
        Object resolve( String value, String multiplicity )
        {
            if ( multiplicity != null && multiplicity.equals( ChangeSetEntityProperty.PROPERTY_MULTIPLICITY_LIST ) )
            {
                List<Object> list = new ArrayList<>();

                for ( String splitValue : value.split( "," ) )
                {
                    list.add( toValue( splitValue ) );
                }

                return list;
            }
            else
            {
                return toValue( value );
            }
        }

        abstract Object toValue( String value );
    }
}
