/*
 * Copyright (c) 2019 Comvai, s.r.o. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.ctoolkit.agent.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The bean holding the Entity change description.
 * Examples:
 * 1) entity with only kind specified
 * <pre>
 * {@code
 * <entity kind="Member">
 *     <property name="prop1" type="type1" value="value1"/>
 * </entity>
 * }
 * </pre>
 * 2) entity with specified key - used for entity update
 * <pre>
 * {@code
 * <entity kind="Member" key="ABdghjahBDKABDkaDA78DASJHDKA">
 *     <property name="prop1" type="type1" value="value2"/>
 * </entity>
 * }
 * </pre>
 * 3) entity with id
 * <pre>
 * {@code
 * <entity kind="Member" id="151">
 *     <property name="prop1" type="type1" value="value2"/>
 * </entity>
 * }
 * </pre>
 * 4) entity with name
 * <pre>
 * {@code
 * <entity kind="Member" name="name">
 *     <property name="prop1" type="type1" value="value2"/>
 * </entity>
 * }
 * </pre>
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class ChangeSetEntity
        implements Serializable
{
    private static final long serialVersionUID = 5954520260345094853L;

    @XmlAttribute( name = "key" )
    private String key;

    @XmlAttribute( name = "kind" )
    private String kind;

    @XmlAttribute( name = "id" )
    private Long id;

    @XmlAttribute( name = "name" )
    private String name;

    @XmlAttribute( name = "parentKey" )
    private String parentKey;

    @XmlAttribute( name = "parentKind" )
    private String parentKind;

    @XmlAttribute( name = "parentId" )
    private Long parentId;

    @XmlAttribute( name = "parentName" )
    private String parentName;

    /**
     * Optional entity properties
     */
    @XmlElement( name = "property" )
    private List<ChangeSetEntityProperty> property;

    public ChangeSetEntity()
    {
    }

    public ChangeSetEntity( ChangeSetEntity other )
    {
        setKey( other.getKey() );
        setKind( other.getKind() );
        setId( other.getId() );
        setName( other.getName() );
        setParentKey( other.getParentKey() );
        setParentKind( other.getParentKind() );
        setParentId( other.getParentId() );
        setParentName( other.getParentName() );

        for ( ChangeSetEntityProperty otherProperty : other.getProperty() )
        {
            getProperty().add( new ChangeSetEntityProperty( otherProperty ) );
        }
    }


    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind( String kind )
    {
        this.kind = kind;
    }

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getParentKey()
    {
        return parentKey;
    }

    public void setParentKey( String parentKey )
    {
        this.parentKey = parentKey;
    }

    public String getParentKind()
    {
        return parentKind;
    }

    public void setParentKind( String parentKind )
    {
        this.parentKind = parentKind;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId( Long parentId )
    {
        this.parentId = parentId;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName( String parentName )
    {
        this.parentName = parentName;
    }

    public List<ChangeSetEntityProperty> getProperty()
    {
        if ( property == null )
        {
            property = new ArrayList<>();
        }
        return property;
    }

    public void setProperty( List<ChangeSetEntityProperty> property )
    {
        this.property = property;
    }

    /**
     * Returns true if this change set entity has attached properties.
     *
     * @return true if this change set entity has attached properties
     */
    public boolean hasProperties()
    {
        return !getProperty().isEmpty();
    }

    @Override
    public String toString()
    {
        return "ChangeSetEntity{" +
                "key='" + key + '\'' +
                ", kind='" + kind + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", parentKey='" + parentKey + '\'' +
                ", parentKind='" + parentKind + '\'' +
                ", parentId=" + parentId +
                ", parentName='" + parentName + '\'' +
                ", property=" + property +
                '}';
    }
}
