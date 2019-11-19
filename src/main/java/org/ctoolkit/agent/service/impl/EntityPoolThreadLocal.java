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
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Key;
import com.google.inject.Inject;
import org.ctoolkit.agent.service.EntityPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * The entity pool for batch processing of inserts and deletes for GAE datastore
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class EntityPoolThreadLocal
        implements EntityPool
{
    private static final Logger logger = LoggerFactory.getLogger( EntityPoolThreadLocal.class );

    /**
     * The default number of entities to put.delete from the data store
     */
    private static final int DEFAULT_COUNT_LIMIT = 100;

    /**
     * The datastore service to work on
     */
    private final Datastore ds;

    /**
     * The max items to put into entity lists before flushing them into data store
     */
    private final int maxItems;

    /**
     * The list of holding entities waiting for putting them into data store
     */
    private final List<FullEntity> toPut = new ArrayList<>();

    /**
     * The list of holding entities waiting for deleting them from data store
     */
    private final List<Key> toDelete = new ArrayList<>();

    /**
     * The default constructor
     */
    @Inject
    public EntityPoolThreadLocal( Datastore ds )
    {
        this( ds, DEFAULT_COUNT_LIMIT );
    }

    private EntityPoolThreadLocal( Datastore ds, int maxItems )
    {
        logger.info( "Building entity pool for " + maxItems + " entries" );
        this.ds = ds;
        this.maxItems = maxItems;
    }

    public void put( FullEntity ent )
    {
        logger.info( "Adding entity into the put-pool" );
        if ( toPut.size() >= maxItems )
        {
            flushPuts();
        }
        toPut.add( ent );
    }

    public void delete( Key key )
    {
        if ( toDelete.size() >= maxItems )
        {
            flushDeletes();
        }
        toDelete.add( key );
    }

    public void flush()
    {
        try
        {
            if ( !toDelete.isEmpty() )
            {
                flushDeletes();
            }
            if ( !toPut.isEmpty() )
            {
                flushPuts();
            }
        }
        catch ( ConcurrentModificationException e )
        {
            logger.info( "::PUT POOL (" + toPut.size() + " items)" );
            logger.info( "::DELETE POOL (" + toDelete.size() + " items)" );
            throw e;
        }
    }

    private void flushPuts()
    {
        logger.info( "Flushing the put-pool (" + toPut.size() + " items)" );
        ds.put( toPut.toArray( new FullEntity[0] ) );
        toPut.clear();
    }

    private void flushDeletes()
    {
        logger.info( "Flushing the delete-pool (" + toDelete.size() + " items)" );
        ds.delete( toDelete.toArray( new Key[0] ) );
        toDelete.clear();
    }

}
