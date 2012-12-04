/*
 *  Copyright (C) 2010-2012 Axel Morgner, structr <structr@structr.org>
 *
 *  This file is part of structr <http://structr.org>.
 *
 *  structr is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  structr is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */



package org.structr.core.entity;

import org.neo4j.graphdb.Direction;

import org.structr.common.PropertyView;
import org.structr.common.RelType;
import org.structr.common.View;
import org.structr.core.property.CollectionProperty;
import org.structr.core.property.EntityProperty;

//~--- classes ----------------------------------------------------------------

/**
 *
 * @author amorgner
 *
 */
public class Folder extends AbstractNode {

	public static final EntityProperty<Folder>     parentFolder = new EntityProperty<Folder>("parentFolder", Folder.class, RelType.CONTAINS, Direction.INCOMING);

	public static final CollectionProperty<Folder> folders      = new CollectionProperty<Folder>(Folder.class, RelType.CONTAINS);
	public static final CollectionProperty<File>   files        = new CollectionProperty<File>(File.class, RelType.CONTAINS);
	public static final CollectionProperty<Image>  images       = new CollectionProperty<Image>(Image.class, RelType.CONTAINS);

	public static final View uiView = new View(Folder.class, PropertyView.Ui,
		parentFolder, folders, files, images
	);
}
