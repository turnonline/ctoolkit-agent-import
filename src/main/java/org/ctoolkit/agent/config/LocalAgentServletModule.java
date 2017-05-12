package org.ctoolkit.agent.config;

import com.google.inject.servlet.ServletModule;
import org.ctoolkit.agent.servlet.ImportServlet;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class LocalAgentServletModule
        extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        serve( "/import" ).with( ImportServlet.class );
    }
}
