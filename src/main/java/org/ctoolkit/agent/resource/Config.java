package org.ctoolkit.agent.resource;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class Config
{
    private String resourceRootPath;

    private String fileMask;

    public static Config getDefault()
    {
        Config config = new Config();

        config.setResourceRootPath("/dataset");
        config.setFileMask("changeset_\\d{5}.xml");

        return config;
    }

    public String getResourceRootPath()
    {
        return resourceRootPath;
    }

    public void setResourceRootPath( String resourceRootPath )
    {
        this.resourceRootPath = resourceRootPath;
    }

    public String getFileMask()
    {
        return fileMask;
    }

    public void setFileMask( String fileMask )
    {
        this.fileMask = fileMask;
    }
}
