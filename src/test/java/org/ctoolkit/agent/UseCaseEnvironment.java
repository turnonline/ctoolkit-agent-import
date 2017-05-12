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

package org.ctoolkit.agent;

import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalModulesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.inject.servlet.ServletModule;
import org.ctoolkit.agent.config.LocalAgentModule;
import org.ctoolkit.test.appengine.ServiceConfigModule;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class UseCaseEnvironment
        extends ServiceConfigModule
{
    // Unlike CountDownLatch, TaskCountDownlatch lets us reset.
    final LocalTaskQueueTestConfig.TaskCountDownLatch latch = new LocalTaskQueueTestConfig.TaskCountDownLatch( 1 );

    public UseCaseEnvironment()
    {
        construct( new LocalServiceTestHelper(
                new LocalMemcacheServiceTestConfig(),
                new LocalModulesServiceTestConfig(),
                new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage( 0 ),
                new LocalBlobstoreServiceTestConfig(),
                new LocalTaskQueueTestConfig()
                        .setDisableAutoTaskExecution( false )
                        .setCallbackClass( LocalTaskQueueTestConfig.DeferredTaskCallback.class )
                        .setTaskExecutionLatch( latch ) ) );
    }

    @Override
    public void configureTestBinder()
    {
        // setting the SystemProperty.Environment.Value.Development
        System.setProperty( "com.google.appengine.runtime.environment", "Development" );

        install( new ServletModule() );
        install( new LocalAgentModule() );
    }

    protected void await( long seconds ) throws InterruptedException
    {
        latch.await( seconds, TimeUnit.SECONDS );
    }
}
