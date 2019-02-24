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
package org.ctoolkit.agent.config;

import com.google.inject.AbstractModule;
import org.ctoolkit.agent.service.ChangeSetService;
import org.ctoolkit.agent.service.DataAccess;
import org.ctoolkit.agent.service.EntityPool;
import org.ctoolkit.agent.service.impl.ChangeSetEntityToEntityMapper;
import org.ctoolkit.agent.service.impl.ChangeSetServiceBean;
import org.ctoolkit.agent.service.impl.DataAccessBean;
import org.ctoolkit.agent.service.impl.EntityEncoder;
import org.ctoolkit.agent.service.impl.EntityPoolThreadLocal;
import org.ctoolkit.agent.service.impl.ImportTask;

import javax.inject.Singleton;

/**
 * The local agent module dedicated solely for local development purpose.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class LocalDevAgentModule
        extends AbstractModule
{
    @Override
    protected void configure()
    {
        // same to LocalAgentModule except EntityPool scope has changed to Singleton
        bind( ChangeSetEntityToEntityMapper.class ).in( Singleton.class );
        bind( EntityEncoder.class ).in( Singleton.class );
        bind( EntityPool.class ).to( EntityPoolThreadLocal.class ).in( Singleton.class );
        bind( DataAccess.class ).to( DataAccessBean.class ).in( Singleton.class );
        bind( ChangeSetService.class ).to( ChangeSetServiceBean.class ).in( Singleton.class );

        requestStaticInjection( ImportTask.class );
    }
}
