/*
 *  Copyright (C) 2010-2014 Morgner UG (haftungsbeschränkt)
 * 
 *  This file is part of structr <http://structr.org>.
 * 
 *  structr is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 * 
 *  structr is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU Affero General Public License
 *  along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.web.datasource;

import java.util.LinkedList;
import java.util.List;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.app.StructrApp;
import org.structr.core.entity.AbstractNode;
import org.structr.web.common.GraphDataSource;
import org.structr.web.common.RenderContext;

/**
 *
 * @author Axel Morgner
 */
public class IdRequestParameterGraphDataSource implements GraphDataSource<List<GraphObject>> {

	private String parameterName = null;

	public IdRequestParameterGraphDataSource(String parameterName) {
		this.parameterName = parameterName;
	}

	@Override
	public List<GraphObject> getData(final SecurityContext securityContext, final RenderContext renderContext, final AbstractNode referenceNode) throws FrameworkException {

		if (securityContext != null && securityContext.getRequest() != null) {

			String nodeId = securityContext.getRequest().getParameter(parameterName);
			if (nodeId != null) {

				return getData(securityContext, renderContext, nodeId);
			}
		}
		return null;
	}

	@Override
	public List<GraphObject> getData(final SecurityContext securityContext, final RenderContext renderContext, final String nodeId) throws FrameworkException {

		AbstractNode node = (AbstractNode) StructrApp.getInstance(securityContext).get(nodeId);

		if (node != null) {

			List<GraphObject> graphData = new LinkedList<>();
			graphData.add(node);

			return graphData;
		}

		return null;
	}
}
