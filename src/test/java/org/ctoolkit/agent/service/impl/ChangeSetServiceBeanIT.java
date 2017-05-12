package org.ctoolkit.agent.service.impl;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.guiceberry.junit4.GuiceBerryRule;
import org.ctoolkit.agent.UseCaseEnvironment;
import org.ctoolkit.agent.resource.Config;
import org.ctoolkit.agent.service.ChangeSetService;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class ChangeSetServiceBeanIT
        extends UseCaseEnvironment
{
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule( UseCaseEnvironment.class );

    @Inject
    private ChangeSetService service;

    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    @Test
    public void startImport() throws Exception
    {
        service.startImport( Config.getDefault() );

        await( 1 );

        Entity country = datastoreService.get( KeyFactory.createKey( "Country", 1 ) );
        assertEquals("EN", country.getProperty( "code" ));

        Entity state = datastoreService.get( KeyFactory.createKey( "State", 1 ) );
        assertEquals("USA", state.getProperty( "code" ));
    }
}