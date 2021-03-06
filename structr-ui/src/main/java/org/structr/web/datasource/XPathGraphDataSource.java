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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.entity.AbstractNode;
import org.structr.web.common.GraphDataSource;
import org.structr.web.common.RenderContext;
import org.structr.web.entity.dom.DOMElement;
import org.structr.web.entity.dom.DOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Axel Morgner
 */
public class XPathGraphDataSource implements GraphDataSource<List<GraphObject>> {

	private static final Logger logger = Logger.getLogger(XPathGraphDataSource.class.getName());

	private Document document;

	@Override
	public List<GraphObject> getData(final SecurityContext securityContext, final RenderContext renderContext, final AbstractNode referenceNode) throws FrameworkException {

		final String xpathQuery = referenceNode.getProperty(DOMElement.xpathQuery);

		if (xpathQuery == null || xpathQuery.isEmpty()) {
			return null;
		}

		document = ((DOMNode) referenceNode).getOwnerDocument();

		return getData(securityContext, renderContext, xpathQuery);
	}

	@Override
	public List<GraphObject> getData(final SecurityContext securityContext, final RenderContext renderContext, final String xpathQuery) throws FrameworkException {

		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();

		try {

			// FIXME: this code works only with absolute xpath queries because
			//        the xpath parser implementation is stupid (comparing object
			//        equality using ==).
			Object result = xpath.evaluate(xpathQuery, document, XPathConstants.NODESET);
			List<GraphObject> results = new LinkedList<>();

			if (result instanceof NodeList) {

				NodeList nodes = (NodeList) result;
				int len = nodes.getLength();

				for (int i = 0; i < len; i++) {

					Node node = nodes.item(i);

					if (node instanceof GraphObject) {

						results.add((GraphObject) node);
					}
				}

			} else if (result instanceof GraphObject) {

				results.add((GraphObject) result);
			}

			return results;

		} catch (Throwable t) {

			logger.log(Level.WARNING, "Unable to execute xpath query: {0}", t.getMessage());
		}

		return Collections.EMPTY_LIST;

	}

}
