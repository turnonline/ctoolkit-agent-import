# Ctoolkit Agent import - library for importing DB in local development
Cloud Toolkit Import Agent is library for development purposes. It allows you to import data into local Google App engine datastore.

## Usage
### Add dependency to your maven pom file. 
```xml
    <dependencies>
        ...
        <dependency>
            <groupId>org.ctoolkit.agent</groupId>
            <artifactId>ctoolkit-agent-import</artifactId>
            <version>${latest.version}</version>
        </dependency>
        ...
    </dependencies>
```
    
We recommend to add dependency only for local development otherwise _/import_ servlet will be exposed to your test/production server:

```xml
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
```
     
### Guice - install modules
To install guice agent modules copy code below to your guice module configuration:
```java
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
```
        
## Writing of changesets
Create files _changeset_0000#.xml_ under _${project.root}/src/main/resources/dataset_ directory:
    
    /dataset
        -changeset_00001.xml
        -changeset_00002.xml
        -changeset_00003.xml
        - ...

Put following code inside changest_00001.xml:

```xml
    <changeset author="john.foo@bar.org" comment="Init data for country">
        <entities>
            <entity id="1" kind="Country">
                <property name="code" type="string" value="FR"/>
                <property name="label" type="string" value="France"/>
            </entity>
        </entities>
    </changeset>
```
For full list of supported types go to [ChangeSetEntityProperty.java](https://github.com/turnonline/ctoolkit-agent-import/blob/master/src/main/java/org/ctoolkit/agent/resource/ChangeSetEntityProperty.java)

> Each changeset is running in separate task queue concurrently so ordering of changesets is only informative.

## Running import
Run your application and navigate to:

    http://localhost:8080/import

## Entity keys and relations
### Key options
- by _id + kind_ property - long value
- by _name + kind_ property - string value
- by _key_ property - kind:id/kind:name

```xml
    <entity id="1" kind="Country">
        ...
    </entity>

    <entity name="EN" kind="Country">
        ...
    </entity>
    
    <entity key="Country:1">
        ...
    </entity>
    
    <entity key="Country:EN">
        ...
    </entity>
```
    
### Parent key options
- by _parentId + parentKind_ property - long value
- by _parentName + parentKind_ property - string value
- by _parentKey_ property - parentKind:parentId/parentKind:parentName

```xml
    <entity id="1" kind="Country" parentId="10" parentKind="Continent">
        ...
    </entity>
        
    <entity id="1" kind="Country" parentName="ASIA" parentKind="Continent">
        ...
    </entity>  
    
    <entity id="1" kind="Country" parentKey="Continent:10">
        ...
    </entity>
        
    <entity id="1" kind="Country" parentKey="Continent:ASIA">
        ...
    </entity>    
```

If parent entity consist from more than one level, you compose key as follows:

_parentKind_L1:parentId_L1::parentKind_L2:parentId_L2: ..._

```xml
    <entity id="1" kind="City" parentKey="Continent:10::Country:1">
        ...
    </entity>
```

### Relations
If you want to add entity relation just add _key_ or _key-name_ property to 'property' element:

```xml
    <entity id="1" kind="City">
        <property name="mayor" type="key" value="Mayors:10"/>      
    </entity>
    
    <entity id="1" kind="City">
        <property name="mayor" type="key-name" value="Mayors:JohnFoo"/>      
    </entity>
``` 