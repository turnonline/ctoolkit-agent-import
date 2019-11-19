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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.KeyValue;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Value;
import org.ctoolkit.agent.resource.ChangeSetEntity;
import org.ctoolkit.agent.resource.ChangeSetEntityProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Mapper for {@link ChangeSetEntity} to {@link Entity} model beans
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class ChangeSetEntityToEntityMapper
{
    private final EntityEncoder encoder;

    private final Datastore datastore;

    private Logger logger = LoggerFactory.getLogger( ChangeSetEntityToEntityMapper.class );

    @Inject
    public ChangeSetEntityToEntityMapper( EntityEncoder encoder, Datastore datastore )
    {
        this.encoder = encoder;
        this.datastore = datastore;
    }

    FullEntity map( ChangeSetEntity changeSetEntity )
    {
        // the kind has to be specified
        if ( changeSetEntity.getKind() == null )
        {
            logger.error( "Missing entity kind! It has to be specified" );
            return null;
        }

        FullEntity.Builder<?> builder = create( changeSetEntity );

        // changeSetEntity up the properties
        if ( changeSetEntity.hasProperties() )
        {
            for ( ChangeSetEntityProperty prop : changeSetEntity.getProperty() )
            {
                Boolean indexed = prop.getIndexed();
                Value<?> value = encoder.decodeProperty(
                        prop.getType(),
                        prop.getMultiplicity(),
                        prop.getValue(),
                        indexed );

                builder.set( prop.getName(), value );
            }
        }

        return builder.build();
    }

    private FullEntity.Builder<IncompleteKey> create( ChangeSetEntity changeSetEntity )
    {
        FullEntity.Builder<IncompleteKey> entity;
        KeyFactory factory = datastore.newKeyFactory();

        // parentEntityId has top priority
        if ( changeSetEntity.getParentKey() != null )
        {
            KeyValue keyValue = encoder.parseKeyByIdOrName( changeSetEntity.getParentKey() );
            if ( keyValue != null )
            {
                factory.addAncestors( keyValue.get().getAncestors() );
            }
        }
        else if ( changeSetEntity.getParentId() != null && changeSetEntity.getParentKind() != null )
        {
            factory.addAncestor( PathElement.of( changeSetEntity.getParentKind(), changeSetEntity.getParentId() ) );
            // parent kind/name has the lowest priority in the reference chain
        }
        else if ( changeSetEntity.getParentName() != null && changeSetEntity.getParentKind() != null )
        {
            factory.addAncestor( PathElement.of( changeSetEntity.getParentKind(), changeSetEntity.getParentName() ) );
        }

        // generate the entity
        if ( changeSetEntity.getKey() != null )
        {
            throw new UnsupportedOperationException( "ChangeSetEntity.key is unsupported" );
        }
        else if ( changeSetEntity.getId() != null )
        {
            Key key = factory.setKind( changeSetEntity.getKind() ).newKey( changeSetEntity.getId() );
            entity = FullEntity.newBuilder( key );
        }
        else if ( changeSetEntity.getName() != null )
        {
            Key key = factory.setKind( changeSetEntity.getKind() ).newKey( changeSetEntity.getName() );
            entity = FullEntity.newBuilder( key );
        }
        else
        {
            IncompleteKey key = factory.setKind( changeSetEntity.getKind() ).newKey();
            entity = FullEntity.newBuilder( key );
        }

        return entity;
    }
}
