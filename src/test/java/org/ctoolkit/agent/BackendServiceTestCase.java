package org.ctoolkit.agent;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalModulesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * The base class for App Engine backend services local testing.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class BackendServiceTestCase
{
    private final static Logger LOGGER = LoggerFactory.getLogger( BackendServiceTestCase.class );

    private LocalTaskQueueTestConfig.TaskCountDownLatch latch = new LocalTaskQueueTestConfig.TaskCountDownLatch( 1 );

    private LocalServiceTestHelper helper = new LocalServiceTestHelper( new LocalMemcacheServiceTestConfig(),
            new LocalMemcacheServiceTestConfig(),
            new LocalModulesServiceTestConfig(),
            new LocalURLFetchServiceTestConfig(),
            new LocalBlobstoreServiceTestConfig(),
            new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage( 0 ),
            new LocalTaskQueueTestConfig()
                    .setDisableAutoTaskExecution( false )
                    .setCallbackClass( LocalTaskQueueTestConfig.DeferredTaskCallback.class )
                    .setTaskExecutionLatch( latch ) );

    protected boolean awaitAndReset( long milliseconds )
    {
        try
        {
            return latch.awaitAndReset( milliseconds, TimeUnit.MILLISECONDS );
        }
        catch ( InterruptedException e )
        {
            LOGGER.error( "", e );
            return false;
        }
    }

    @BeforeMethod
    public void setUp( Method m )
    {
        helper.setUp();
        SystemProperty.environment.set( "Development" );
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        latch.reset();
        helper.tearDown();
    }
}
