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


package org.structr.schema.action;

import java.util.Date;
import java.util.List;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.Ownership;
import org.structr.core.app.StructrApp;
import org.structr.core.converter.PropertyConverter;
import org.structr.core.entity.AbstractNode;
import org.structr.core.entity.Principal;
import org.structr.core.entity.relationship.PrincipalOwnsNode;
import org.structr.core.graph.NodeInterface;
import org.structr.core.property.PropertyKey;

/**
 *
 * @author Christian Morgner
 */
public class ActionContext {

	private GraphObject parent = null;
	private Object data        = null;

	public ActionContext() {
		this(null, null);
	}

	public ActionContext(final GraphObject parent, final Object data) {
		this.parent = parent;
		this.data   = data;
	}

	public boolean returnRawValue(final SecurityContext securityContext) {
		return false;
	}

	public Object getReferencedProperty(final SecurityContext securityContext, final NodeInterface entity, final String refKey) throws FrameworkException {

		final String[] parts = refKey.split("[\\.]+");
		Object _data         = null;

		// walk through template parts
		for (int i = 0; (i < parts.length); i++) {

			String part = parts[i];

			if (_data != null && _data instanceof GraphObject) {

				PropertyKey referenceKeyProperty = StructrApp.getConfiguration().getPropertyKeyForJSONName(_data.getClass(), part);
				PropertyConverter converter      = referenceKeyProperty.inputConverter(securityContext);
				_data                            = ((GraphObject)_data).getProperty(referenceKeyProperty);

				if (_data != null && converter != null) {
					_data = converter.revert(_data);
				}

				if (_data == null) {

					// Need to return null here to avoid _data sticking to the (wrong) parent object
					return null;

				}

			}

			// special keyword "size"
			if (i > 0 && "size".equals(part) && _data instanceof List) {
				return ((List)_data).size();
			}

			// special keyword "now":
			if ("now".equals(part)) {

				// Return current date converted in format
				// Note: We use "createdDate" here only as an arbitrary property key to get the database converter
				return AbstractNode.createdDate.inputConverter(securityContext).revert(new Date());

			}

			// special keyword "me"
			if ("me".equals(part)) {

				Principal me = (Principal) securityContext.getUser(false);

				if (me != null) {

					_data = me;

					continue;
				}

			}

			// special boolean keywords
			if ("true".equals(part)) {
				return true;
			}

			if ("false".equals(part)) {
				return false;
			}

			// the following keywords work only on root level
			// so that they can be used as property keys for data objects
			if (_data == null) {

				// special keyword "parent":
				if ("parent".equals(part)) {

					_data = parent;

					if (parts.length == 1) {
						return _data;
					}

					continue;
				}

				// details data object id
				if ("id".equals(part)) {

					return entity.getUuid();
				}

				// special keyword "this"
				if ("this".equals(part)) {

					_data = entity;

					if (parts.length == 1) {
						return _data;
					}

					continue;

				}

				// special keyword "data"
				if ("data".equals(part)) {

					_data = data;

					if (parts.length == 1) {
						return _data;
					}

					continue;

				}

				// special keyword "element"
				if ("element".equals(part)) {

					_data = entity;

					if (parts.length == 1) {
						return _data;
					}

					continue;

				}

				// special keyword "owner"
				if ("owner".equals(part)) {

					Ownership rel = entity.getIncomingRelationship(PrincipalOwnsNode.class);
					if (rel != null) {

						_data = rel.getSourceNode();

						if (parts.length == 1) {
							return _data;
						}
					}

					continue;

				}

			}

		}

		return _data;
	}
}
