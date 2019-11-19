/*
 * Copyright (c) 2019 Comvai, s.r.o. All Rights Reserved.
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

import com.google.api.client.util.Base64;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.BlobValue;
import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.KeyValue;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;
import com.google.common.base.Splitter;
import org.ctoolkit.agent.resource.ChangeSetEntityProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.Iterator;

/**
 * Datastore entity encoder
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class EntityEncoder
{
    private static final Logger logger = LoggerFactory.getLogger( EntityEncoder.class );

    private final Datastore datastore;

    @Inject
    public EntityEncoder( Datastore datastore )
    {
        this.datastore = datastore;
    }

    /**
     * Transforms a type and a string value to real object with given type and value.
     *
     * @param type         the type of the property
     * @param multiplicity multiplicity of property
     * @param value        the string represented value of the property
     * @return ChangeSetEntityProperty representation of the property
     */
    Value<?> decodeProperty( String type, String multiplicity, String value, Boolean indexed )
    {
        if ( value == null )
        {
            return null;
        }

        boolean exclude = indexed == null || !indexed;

        if ( ChangeSetEntityProperty.PROPERTY_TYPE_STRING.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Value<?> toValue( String value )
                {
                    return StringValue
                            .newBuilder( value )
                            .setExcludeFromIndexes( exclude )
                            .build();
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_DOUBLE.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Value<?> toValue( String value )
                {
                    return DoubleValue
                            .newBuilder( Double.parseDouble( value ) )
                            .setExcludeFromIndexes( exclude )
                            .build();
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_LONG.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Value<?> toValue( String value )
                {
                    return LongValue
                            .newBuilder( Long.parseLong( value ) )
                            .setExcludeFromIndexes( exclude )
                            .build();
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_BOOLEAN.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Value<?> toValue( String value )
                {
                    return BooleanValue
                            .newBuilder( Boolean.parseBoolean( value ) )
                            .setExcludeFromIndexes( exclude )
                            .build();
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_DATE.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Value<?> toValue( String value )
                {
                    return TimestampValue
                            .newBuilder( Timestamp.of( new Date( Long.parseLong( value ) ) ) )
                            .setExcludeFromIndexes( exclude )
                            .build();
                }
            }.resolve( value, multiplicity );
        }
        else if ( ChangeSetEntityProperty.PROPERTY_TYPE_BLOB.equals( type ) )
        {
            return new ValueResolver()
            {
                @Override
                Value<?> toValue( String value )
                {
                    try
                    {
                        return BlobValue
                                .newBuilder( Blob.copyFrom( Base64.decodeBase64( value ) ) )
                                .setExcludeFromIndexes( exclude )
                                .build();
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
                Value<?> toValue( String value )
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
                Value<?> toValue( String value )
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
    KeyValue parseKeyByIdOrName( String stringKey )
    {
        Iterator<String> keys = Splitter.on( "::" ).trimResults().split( stringKey ).iterator();

        String kind;
        String idName;
        Key key = null;
        KeyFactory factory = datastore.newKeyFactory();

        while ( keys.hasNext() )
        {
            String singleKey = keys.next();
            String[] spl = singleKey.split( ":" );
            kind = spl[0].trim();
            idName = spl[1].trim();

            if ( keys.hasNext() )
            {
                try
                {
                    factory.addAncestor( PathElement.of( kind, Long.parseLong( idName ) ) );
                }
                catch ( NumberFormatException e )
                {
                    factory.addAncestor( PathElement.of( kind, idName ) );
                }
            }
            else
            {
                factory.setKind( kind );
                try
                {
                    key = factory.newKey( Long.parseLong( idName ) );
                }
                catch ( NumberFormatException e )
                {
                    key = factory.newKey( idName );
                }
            }
        }

        return key == null ? null : KeyValue.of( key );
    }

    /**
     * Parse given string to Key first try as Long Id then if parsing fails as String name.
     *
     * @param stringKey the input string key to parse
     * @return the parsed key
     */
    private KeyValue parseKeyByName( String stringKey )
    {
        Iterator<String> keys = Splitter.on( "::" ).trimResults().split( stringKey ).iterator();

        String kind;
        String name;
        Key key = null;
        KeyFactory factory = datastore.newKeyFactory();

        while ( keys.hasNext() )
        {
            String singleKey = keys.next();
            String[] spl = singleKey.split( ":" );
            kind = spl[0].trim();
            name = spl[1].trim();

            if ( keys.hasNext() )
            {
                factory.addAncestor( PathElement.of( kind, name ) );
            }
            else
            {
                factory.setKind( kind );
                key = factory.newKey( name );
            }
        }

        return key == null ? null : KeyValue.of( key );
    }

    private static abstract class ValueResolver
    {
        Value<?> resolve( String value, String multiplicity )
        {
            if ( multiplicity != null && multiplicity.equals( ChangeSetEntityProperty.PROPERTY_MULTIPLICITY_LIST ) )
            {
                ListValue.Builder builder = ListValue.newBuilder();

                for ( String splitValue : value.split( "," ) )
                {
                    builder.addValue( toValue( splitValue ) );
                }

                return builder.build();
            }
            else
            {
                return toValue( value );
            }
        }

        abstract Value<?> toValue( String value );
    }
}
