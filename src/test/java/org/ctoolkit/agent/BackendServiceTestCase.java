package org.ctoolkit.agent;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalModulesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The base class for App Engine backend services local testing.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class BackendServiceTestCase
{
    private final static Logger LOGGER = LoggerFactory.getLogger( BackendServiceTestCase.class );

    protected LocalDatastoreHelper lDatastoreHelper;

    private LocalTaskQueueTestConfig.TaskCountDownLatch latch = new LocalTaskQueueTestConfig.TaskCountDownLatch( 1 );

    private LocalServiceTestHelper helper = new LocalServiceTestHelper( new LocalMemcacheServiceTestConfig(),
            new LocalMemcacheServiceTestConfig(),
            new LocalModulesServiceTestConfig(),
            new LocalURLFetchServiceTestConfig(),
            new LocalBlobstoreServiceTestConfig(),
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
    public void setUp() throws IOException
    {
        helper.setUp();
        lDatastoreHelper.reset();
    }

    @AfterMethod
    public void tearDown()
    {
        latch.reset();
        helper.tearDown();
    }

    @BeforeClass
    public void beforeAll() throws IOException, InterruptedException
    {
        lDatastoreHelper.start();
        SystemProperty.environment.set( "Development" );
    }

    @AfterClass
    public void stop() throws InterruptedException, TimeoutException, IOException
    {
        lDatastoreHelper.stop();
    }
}
