/**
 * Copyright (C) 2010-2014 Morgner UG (haftungsbeschränkt)
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core.entity;

import org.neo4j.graphdb.Node;
import org.neo4j.helpers.Predicate;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.graph.NodeInterface;

/**
 *
 * @author Christian Morgner
 */
public interface Source<R, S> {
	
	public S get(final SecurityContext securityContext, final NodeInterface node, final Predicate<GraphObject> predicate);
	
	public void set(final SecurityContext securityContext, final NodeInterface node, final S value) throws FrameworkException;

	public R getRawSource(final SecurityContext securityContext, final Node dbNode, final Predicate<GraphObject> predicate);
	public boolean hasElements(final SecurityContext securityContext, final Node dbNode, final Predicate<GraphObject> predicate);
}
