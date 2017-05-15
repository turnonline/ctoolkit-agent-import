# Ctoolkit Agent import - library for importing DB in local development
Cloud Toolkit Import Agent is library for development purpose. It allows you to import data into local Google App engine datastore.

## Usage
### Add dependency to your maven pom file. 

    <dependencies>
        ...
        <dependency>
            <groupId>org.ctoolkit.agent</groupId>
            <artifactId>ctoolkit-agent-import</artifactId>
            <version>${latest.version}</version>
        </dependency>
        ...
    </dependencies>
    
We recommend to add dependency only to for local development otherwise _/import_ servlet will be exposed to your test/production server:
 
    <profile>
        <id>development</id>
        <dependencies>
            ...
            <dependency>
                <groupId>org.ctoolkit.agent</groupId>
                <artifactId>ctoolkit-agent-import</artifactId>
                <version>${latest.version}</version>
            </dependency>
        </dependencies>
            ...
    </profile>
     
### Guice - install modules
To install guice agent modules copy code below to your guice module configuration:

    package com.foo.bar;

    import com.google.appengine.api.utils.SystemProperty;
    import com.google.inject.Injector;
    import com.google.inject.servlet.GuiceServletContextListener;

    public abstract class GaeGuiceServletContextListener
        extends GuiceServletContextListener
    {
        @Override
        protected final Injector getInjector()
        {
            if ( SystemProperty.environment.value() == SystemProperty.Environment.Value.Production )
            {
                // The app is running on App Engine...
                return getProductionInjector();
            }
            else
            {
                // The app is running on local development
                return getDevInjector();
            }
        }
    
        protected abstract Injector getDevInjector()
        {
            // TODO: local development settings goes here
            return Guice.createInjector(
                // other modules for local development

                // local agent
                new LocalAgentModule(),
                new LocalAgentServletModule()
            );
        }
    
        protected abstract Injector getProductionInjector()
        {
            // TODO: production setting goes here...
            return Guice.createInjector(
                // other modules for test/prod environments
            );
        }
    }
    
## Writing of changesets
Create files _changeset_0000#.xml_ under _${project.root}/src/main/resources/dataset_ directory:
    
    /dataset
        -changeset_00001.xml
        -changeset_00002.xml
        -changeset_00003.xml
        - ...

Put following code inside changest_00001.xml:

    <changeset author="john.foo@bar.org" comment="Init data for country">
        <entities>
            <entity id="1" kind="Country">
                <property name="code" type="string" value="FR"/>
                <property name="label" type="string" value="France"/>
            </entity>
        </entities>
    </changeset>


Note: Each changeset is running in separate queue task concurrently so ordering of changesets is only informal.

## Running import
Run your application and navigate to:

    http://localhost:8080/import


## Supported property types
For full list of supported types go to [ChangeSetEntityProperty.java](https://github.com/turnonline/ctoolkit-agent-import/blob/master/src/main/java/org/ctoolkit/agent/resource/ChangeSetEntityProperty.java)

## IDs and relations
You can create ID of entity as follows:
- by _id_ property - long value
- by _name_ property - string value


    <entity id="1" kind="Country">
        ...
    </entity>

    <entity name="EN" kind="Country">
        ...
    </entity>
    
You can create parent key of entity as follows:
- by _key_ property - entity parent. For example for Contact of User you create key by _Contact:1::User:10_


    <entity id="1" kind="Country">
        <property name="continent" type="key" value="Continent:ASIA"/> 
    </entity>    