package org.ctoolkit.agent.service.impl;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.inject.Injector;
import org.ctoolkit.agent.resource.ChangeSet;
import org.ctoolkit.agent.resource.ChangeSetEntity;
import org.ctoolkit.agent.resource.ChangeSetModelKindOp;
import org.ctoolkit.agent.service.DataAccess;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class ImportTask
        implements DeferredTask
{
    private String fileName;

    @Inject
    private static Injector injector;

    @Inject
    transient private DataAccess dataAccess;

    public ImportTask()
    {
    }

    public ImportTask( String fileName )
    {
        this.fileName = fileName;
    }

    @Override
    public void run()
    {
        injector.injectMembers( this );

        InputStream is = ImportTask.class.getResourceAsStream( fileName );
        ChangeSet changeSet = unmarshall( is );

        importChangeSet( changeSet );
    }

    private void importChangeSet( ChangeSet changeSet )
    {
        dataAccess.flushPool();

        // apply model changes
        if ( changeSet.hasModelObject() )
        {
            // process KindOps
            if ( changeSet.getModel().hasKindOpsObject() )
            {
                for ( ChangeSetModelKindOp kindOp : changeSet.getModel().getKindOp() )
                {
                    switch ( kindOp.getOp() )
                    {
                        case ChangeSetModelKindOp.OP_DROP:
                        {
                            dataAccess.dropEntity( kindOp.getKind() );
                            break;
                        }
                        case ChangeSetModelKindOp.OP_CLEAN:
                        {
                            dataAccess.clearEntity( kindOp.getKind() );
                            break;
                        }
                        default:
                        {
                            throw new IllegalArgumentException( "Unsupported Kind operation! " + kindOp.getOp() );
                        }
                    }
                }
            }
        }

        // apply entity changes
        if ( changeSet.hasEntities() )
        {
            for ( ChangeSetEntity cse : changeSet.getEntities().getEntity() )
            {
                dataAccess.addEntity( cse );
            }
        }

        dataAccess.flushPool();
    }

    private static ChangeSet unmarshall( InputStream source )
    {
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance( ChangeSet.class );
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<ChangeSet> jaxbElement = unmarshaller.unmarshal( new StreamSource( source ), ChangeSet.class );

            return jaxbElement.getValue();
        }
        catch ( JAXBException e )
        {
            throw new RuntimeException( "Error occur during unmarshalling class: " + ChangeSet.class, e );
        }
    }
}
