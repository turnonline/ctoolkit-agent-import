package org.ctoolkit.agent.service.impl;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.inject.servlet.ServletModule;
import org.ctoolkit.agent.BackendServiceTestCase;
import org.ctoolkit.agent.LocalAgentTestModule;
import org.ctoolkit.agent.resource.Config;
import org.ctoolkit.agent.service.ChangeSetService;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.testng.Assert.assertEquals;


/**
 * {@link ChangeSetService} integration testing against local emulated datastore.
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
@Guice( modules = {
        ServletModule.class,
        LocalAgentTestModule.class
} )
public class ChangeSetServiceBeanDbTest
        extends BackendServiceTestCase
{
    @Inject
    private ChangeSetService service;

    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    @Test
    public void startImport() throws Exception
    {
        service.startImport( Config.getDefault() );

        awaitAndReset( 2000 );

        Entity country = datastoreService.get( KeyFactory.createKey( "Country", 1 ) );
        assertEquals( country.getProperty( "code" ), "EN" );

        Entity state = datastoreService.get( KeyFactory.createKey( "State", 1 ) );
        assertEquals( state.getProperty( "code" ), "USA" );
    }
}