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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import org.ctoolkit.agent.resource.ChangeSetEntity;
import org.ctoolkit.agent.service.DataAccess;
import org.ctoolkit.agent.service.EntityPool;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

/**
 * GAE datastore implementation of {@link DataAccess}
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class DataAccessBean
        implements DataAccess
{
    private static final int DEFAULT_COUNT_LIMIT = 100;

    private final DatastoreService datastore;

    private final Provider<EntityPool> pool;

    private final ChangeSetEntityToEntityMapper changeSetEntityToEntityMapper;

    @Inject
    protected DataAccessBean( Provider<EntityPool> pool,
                              ChangeSetEntityToEntityMapper changeSetEntityToEntityMapper )
    {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
        this.pool = pool;
        this.changeSetEntityToEntityMapper = changeSetEntityToEntityMapper;
    }

    @Override
    public void addEntity( ChangeSetEntity csEntity )
    {
        Entity entity = changeSetEntityToEntityMapper.map( csEntity );
        pool.get().put( entity );
    }

    @Override
    public void clearEntity( String kind )
    {
        dropEntity( kind );
    }

    @Override
    public void dropEntity( String kind )
    {
        while ( true )
        {
            Query query = new Query( kind ).setKeysOnly();
            PreparedQuery preparedQuery = datastore.prepare( query );
            List<Entity> entList = preparedQuery.asList( withLimit( DEFAULT_COUNT_LIMIT ) );
            if ( !entList.isEmpty() )
            {
                for ( Entity entity : entList )
                {
                    pool.get().delete( entity.getKey() );
                }

                if ( entList.size() < DEFAULT_COUNT_LIMIT )
                {
                    pool.get().flush();
                }
            }
            else
            {
                break;
            }
        }

        pool.get().flush();
    }

    @Override
    public void flushPool()
    {
        pool.get().flush();
    }
}
