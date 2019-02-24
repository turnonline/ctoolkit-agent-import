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
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The bean holding model change descriptions.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class ChangeSetModel
        implements Serializable
{
    private static final long serialVersionUID = 4287991446983301716L;

    /**
     * Optional Kind operations descriptors
     */
    @XmlElement( name = "kindOp" )
    private List<ChangeSetModelKindOp> kindOp;

    public List<ChangeSetModelKindOp> getKindOp()
    {
        if ( kindOp == null )
        {
            kindOp = new ArrayList<>();
        }
        return kindOp;
    }

    public void setKindOp( List<ChangeSetModelKindOp> kindOp )
    {
        this.kindOp = kindOp;
    }

    public boolean hasKindOpsObject()
    {
        return !getKindOp().isEmpty();
    }

    @Override
    public String toString()
    {
        return "ChangeSetModel{" +
                "kindOp=" + kindOp +
                '}';
    }
}
