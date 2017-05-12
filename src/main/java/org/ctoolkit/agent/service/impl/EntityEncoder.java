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
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.repackaged.com.google.api.client.util.Base64;
import org.ctoolkit.agent.resource.ChangeSetEntityProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
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
     * @param type  the type of the property
     * @param value the string represented value of the property
     * @return ChangeSetEntityProperty representation of the property
     */
    public Object decodeProperty( String type, String value )
    {
        if ( value == null )
        {
            return null;
        }
        if ( ChangeSetEntityProperty.PROPERTY_TYPE_STRING.equals( type ) )
        {
            return value;
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_DOUBLE.equals( type ) )
        {
            return Double.valueOf( value );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_FLOAT.equals( type ) )
        {
            return Float.valueOf( value );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_INTEGER.equals( type ) )
        {
            return Integer.valueOf( value );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_BOOLEAN.equals( type ) )
        {
            return Boolean.valueOf( value );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_DATE.equals( type ) )
        {
            return new Date( Long.valueOf( value ) );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_LONG.equals( type ) )
        {
            return Long.valueOf( value );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_SHORTBLOB.equals( type ) )
        {
            try
            {
                return new ShortBlob( Base64.decodeBase64( value ) );
            }
            catch ( Exception e )
            {
                logger.error( "Error by encoding short blob: '" + value + "'" );
                return null;
            }
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_BLOB.equals( type ) )
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
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_KEY.equals( type ) )
        {
            return parseKeyByIdOrName( value );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_KEY_NAME.equals( type ) )
        {
            return parseKeyNames( value );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_TEXT.equals( type ) )
        {
            return new Text( value );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_LIST_LONG.equals( type ) )
        {
            List<Long> list = new ArrayList<>();
            for ( String s : value.split( "," ) )
            {
                try
                {
                    list.add( Long.valueOf( s ) );
                }
                catch ( NumberFormatException e )
                {
                    logger.error( "Unable to convert value to long: '" + s + "'" );
                }
            }

            return list;
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_LIST_ENUM.equals( type ) )
        {
            List<String> list = new ArrayList<>();
            Collections.addAll( list, value.split( "," ) );

            return list;
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_LIST_STRING.equals( type ) )
        {
            List<String> list = new ArrayList<>();
            Collections.addAll( list, value.split( "," ) );

            return list;
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_LIST_KEY.equals( type ) )
        {
            List<Key> list = new ArrayList<>();

            for ( String fullKey : value.split( "," ) )
            {
                list.add( parseKeyByIdOrName( fullKey ) );
            }

            return list;
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

    private Key parseKeyNames( String stringKey )
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
}
