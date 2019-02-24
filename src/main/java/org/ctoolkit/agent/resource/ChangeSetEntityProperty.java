/*
 * Copyright (c) 2017 Comvai, s.r.o. All Rights Reserved.
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
import java.io.Serializable;

/**
 * The bean holding one entity property.
 * Example:
 * <pre>
 * {@code
 * <property name="prop1" type="type" {value="value"}/>
 * }
 * </pre>
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class ChangeSetEntityProperty
        implements Serializable
{
    /**
     * Supported change set entity property types
     */
    public static final String PROPERTY_TYPE_STRING = "string";

    public static final String PROPERTY_TYPE_DOUBLE = "double";

    public static final String PROPERTY_TYPE_LONG = "long";

    public static final String PROPERTY_TYPE_DATE = "date";

    public static final String PROPERTY_TYPE_BOOLEAN = "boolean";

    public static final String PROPERTY_TYPE_BLOB = "blob";

    public static final String PROPERTY_TYPE_NULL = "null";

    public static final String PROPERTY_TYPE_REFERENCE = "reference";

    public static final String PROPERTY_TYPE_REFERENCE_NAME = "reference-name";

    public static final String PROPERTY_MULTIPLICITY_SINGLE = "single";

    public static final String PROPERTY_MULTIPLICITY_LIST = "list";

    private static final long serialVersionUID = -3754798492396641796L;

    @XmlAttribute( name = "name" )
    private String name;

    @XmlAttribute( name = "type" )
    private String type;

    @XmlAttribute( name = "multiplicity" )
    private String multiplicity;

    @XmlAttribute( name = "value" )
    private String value;

    @XmlAttribute( name = "indexed" )
    private Boolean indexed;

    /**
     * Default constructor
     */
    public ChangeSetEntityProperty()
    {
    }

    public ChangeSetEntityProperty( ChangeSetEntityProperty other )
    {
        setName( other.getName() );
        setType( other.getType() );

        setValue( other.getValue() );
    }

    /**
     * Constructor
     *
     * @param name  the property name to be set
     * @param type  the property type to be set
     * @param value the property value to be set
     */
    public ChangeSetEntityProperty( String name, String type, String value )
    {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getMultiplicity()
    {
        return multiplicity;
    }

    public void setMultiplicity( String multiplicity )
    {
        this.multiplicity = multiplicity;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public Boolean getIndexed()
    {
        return indexed;
    }

    public void setIndexed( Boolean indexed )
    {
        this.indexed = indexed;
    }

    @Override
    public String toString()
    {
        return "ChangeSetEntityProperty{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", multiplicity='" + multiplicity + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
