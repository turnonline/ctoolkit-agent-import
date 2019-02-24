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
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Change set change descriptors.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@XmlRootElement( name = "changeset" )
@XmlAccessorType( XmlAccessType.FIELD )
public class ChangeSet
        implements Serializable
{
    private static final long serialVersionUID = 6313658858353310506L;

    @XmlAttribute( name = "author" )
    private String author;

    @XmlAttribute( name = "comment" )
    private String comment;

    @XmlElement( name = "model" )
    private ChangeSetModel model;

    @XmlElement( name = "entities" )
    private ChangeSetEntities entities;

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor( String author )
    {
        this.author = author;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    public ChangeSetModel getModel()
    {
        return model;
    }

    public void setModel( ChangeSetModel model )
    {
        this.model = model;
    }

    public boolean hasModelObject()
    {
        return getModel() != null && ( getModel().hasKindOpsObject());
    }

    public ChangeSetEntities getEntities()
    {
        return entities;
    }

    public void setEntities( ChangeSetEntities entities )
    {
        this.entities = entities;
    }

    public boolean hasEntities()
    {
        return getEntities() != null && !getEntities().getEntity().isEmpty();
    }

    @Override
    public String toString()
    {
        return "ChangeSet{" +
                "author='" + author + '\'' +
                ", comment='" + comment + '\'' +
                ", model=" + model +
                ", entities=" + entities +
                '}';
    }
}
