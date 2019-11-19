package org.ctoolkit.agent.service.impl;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.inject.servlet.ServletModule;
import org.ctoolkit.agent.BackendServiceTestCase;
import org.ctoolkit.agent.TestModule;
import org.ctoolkit.agent.resource.Config;
import org.ctoolkit.agent.service.ChangeSetService;
import org.testng.annotations.BeforeSuite;
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
        TestModule.class
} )
public class ChangeSetServiceBeanDbTest
        extends BackendServiceTestCase
{
    @Inject
    private ChangeSetService service;

    @Inject
    private Datastore datastore;

    @Inject
    private LocalDatastoreHelper helper;

    @BeforeSuite
    public void initHelper()
    {
        super.lDatastoreHelper = helper;
    }

    @Test
    public void startImport()
    {

        service.startImport( Config.getDefault() );

        awaitAndReset( 2000 );

        KeyFactory factory = datastore.newKeyFactory();

        Entity country = datastore.get( factory.setKind( "Country" ).newKey( 1 ) );
        assertEquals( country.getString( "code" ), "EN" );

        factory.reset();
        Entity state = datastore.get( factory.setKind( "State" ).newKey( 1 ) );
        assertEquals( state.getString( "code" ), "USA" );
    }
}