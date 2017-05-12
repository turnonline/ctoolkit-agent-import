package org.ctoolkit.agent.servlet;

import org.ctoolkit.agent.resource.Config;
import org.ctoolkit.agent.service.ChangeSetService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
@Singleton
public class ImportServlet
        extends HttpServlet
{
    private ChangeSetService service;

    @Inject
    public ImportServlet( ChangeSetService service )
    {
        this.service = service;
    }

    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        Config config = Config.getDefault();

        String paramResourceRootPath = req.getParameter( "resourceRootPath" );
        String paramFileMask = req.getParameter( "fileMask" );

        if ( paramResourceRootPath != null )
        {
            config.setResourceRootPath( paramResourceRootPath );
        }
        if ( paramFileMask != null )
        {
            config.setFileMask( paramFileMask );
        }

        service.startImport( config );
    }
}
