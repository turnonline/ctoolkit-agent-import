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
import java.io.Serializable;

/**
 * The bean holding entity kind model update descriptor.
 * Examples:
 * <pre>
 * {@code
 * <kindop op="clean" kind="kind" reason="wrong data"/>
 * }
 * </pre>
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class ChangeSetModelKindOp
        implements Serializable
{
    /**
     * Operations defined on entity kinds
     */
    public final static String OP_DROP = "drop";

    public final static String OP_CLEAN = "clean";

    private static final long serialVersionUID = 6601241156513046986L;

    @XmlAttribute( name = "op" )
    private String op;

    @XmlAttribute( name = "kind" )
    private String kind;

    public String getOp()
    {
        return op;
    }

    public void setOp( String op )
    {
        this.op = op;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind( String kind )
    {
        this.kind = kind;
    }

    @Override
    public String toString()
    {
        return "KindOp{" +
                "op='" + op + '\'' +
                ", kind='" + kind + '\'' +
                '}';
    }
}
