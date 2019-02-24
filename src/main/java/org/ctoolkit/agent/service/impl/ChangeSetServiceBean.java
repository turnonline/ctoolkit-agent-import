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

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.ctoolkit.agent.resource.Config;
import org.ctoolkit.agent.service.ChangeSetService;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link ChangeSetService}
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class ChangeSetServiceBean
        implements ChangeSetService
{
    private ModulesService modulesService = ModulesServiceFactory.getModulesService();

    @Inject
    public ChangeSetServiceBean()
    {
    }

    @Override
    public void startImport( Config config )
    {
        String path = config.getResourceRootPath();
        InputStream is = ChangeSetServiceBean.class.getResourceAsStream( path );

        if ( is == null )
        {
            throw new IllegalArgumentException( "No directory defined in classpath with path '" + path + "'" );
        }

        BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
        String resource;

        try
        {
            while ( ( resource = br.readLine() ) != null )
            {
                Pattern pattern = Pattern.compile( config.getFileMask() );
                Matcher matcher = pattern.matcher( resource );

                String fileName = matcher.matches() ? path + "/" + resource : null;

                if ( fileName != null )
                {
                    String module = modulesService.getCurrentModule();
                    String version = modulesService.getCurrentVersion();
                    String hostname = modulesService.getVersionHostname( module, version );

                    TaskOptions options = TaskOptions.Builder.withDefaults();
                    options.payload( new ImportTask( fileName ) );

                    // header added to make sure run against current module (even non default module)
                    // see https://code.google.com/p/googleappengine/issues/detail?id=10457
                    options.header( "Host", hostname );

                    Queue queue = QueueFactory.getDefaultQueue();
                    queue.add( options );
                }
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Unable to read line from path: '" + path + "'" );
        }
    }
}
