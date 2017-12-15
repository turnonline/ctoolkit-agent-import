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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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

    private Logger logger = LoggerFactory.getLogger( ChangeSetEntityToEntityMapper.class );

    @Inject
    public ChangeSetEntityToEntityMapper( EntityEncoder encoder )
    {
        this.encoder = encoder;
    }

    public Entity map( ChangeSetEntity changeSetEntity )
    {
        // the kind has to be specified
        if ( changeSetEntity.getKind() == null )
        {
            logger.error( "Missing entity kind! It has to be specified" );
            return null;
        }

        Entity entity = create( changeSetEntity );

        // changeSetEntity up the properties
        if ( changeSetEntity.hasProperties() )
        {
            for ( ChangeSetEntityProperty prop : changeSetEntity.getProperty() )
            {
                Boolean indexed = prop.getIndexed();
                Object value = encoder.decodeProperty( prop.getType(), prop.getMultiplicity(), prop.getValue() );

                if ( indexed == null )
                {
                    entity.setProperty( prop.getName(), value );
                }
                else
                {
                    if ( indexed )
                    {
                        entity.setIndexedProperty( prop.getName(), value );
                    }
                    else
                    {
                        entity.setUnindexedProperty( prop.getName(), value );
                    }
                }
            }
        }

        return entity;
    }

    public Entity create( ChangeSetEntity changeSetEntity )
    {
        Entity entity;

        // generate parent key
        Key parentKey = null;

        // parentEntityId has top priority
        if ( changeSetEntity.getParentKey() != null )
        {
            parentKey = encoder.parseKeyByIdOrName( changeSetEntity.getParentKey() );
            // parent kind/id
        }
        else if ( changeSetEntity.getParentId() != null && changeSetEntity.getParentKind() != null )
        {
            parentKey = KeyFactory.createKey( changeSetEntity.getParentKind(), changeSetEntity.getParentId() );
            // parent kind/name has the lowest priority in the reference chain
        }
        else if ( changeSetEntity.getParentName() != null && changeSetEntity.getParentKind() != null )
        {
            parentKey = KeyFactory.createKey( changeSetEntity.getParentKind(), changeSetEntity.getParentName() );
        }

        // generate the entity

        // look for a key property
        if ( changeSetEntity.getKey() != null )
        {
            // ignore parent key, because it has to be composed within the entity key
            entity = new Entity( KeyFactory.stringToKey( changeSetEntity.getKey() ) );
        }
        else if ( changeSetEntity.getId() != null )
        {
            // check if there is id changeSetEntity
            // look for parent kind/id
            if ( parentKey != null )
            {
                // build the entity key
                Key key = new KeyFactory.Builder( parentKey ).addChild( changeSetEntity.getKind(), changeSetEntity.getId() ).getKey();
                entity = new Entity( key );
            }
            else
            {
                entity = new Entity( KeyFactory.createKey( changeSetEntity.getKind(), changeSetEntity.getId() ) );
            }
        }
        else if ( changeSetEntity.getName() != null )
        {
            // build the entity key
            if ( parentKey != null )
            {
                entity = new Entity( changeSetEntity.getKind(), changeSetEntity.getName(), parentKey );
            }
            else
            {
                entity = new Entity( changeSetEntity.getKind(), changeSetEntity.getName() );
            }
        }
        else
        {
            entity = new Entity( changeSetEntity.getKind() );
        }

        return entity;
    }
}
