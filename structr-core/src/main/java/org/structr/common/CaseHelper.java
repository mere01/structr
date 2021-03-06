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
package org.structr.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

//~--- classes ----------------------------------------------------------------

/**
 * A helper class that contains methods to convert strings to and from
 * different cases and styles, i.e. camelCase to underscore_style etc.
 *
 * @author Axel Morgner
 */
public class CaseHelper {

	public static String toUpperCamelCase(final String input) {
		return WordUtils.capitalize(input, new char[] { '_' }).replaceAll("_", "");
	}

	public static String toLowerCamelCase(final String input) {
		return input.substring(0, 1).toLowerCase().concat(WordUtils.capitalize(input, new char[] { '_' }).replaceAll("_", "").substring(1));
	}

	public static String toUnderscore(final String input, final boolean plural) {

		StringBuilder out = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {

			char c = input.charAt(i);

			if (Character.isUpperCase(c)) {

				if (i > 0) {

					out.append("_");

				}

				out.append(Character.toLowerCase(c));

			} else {

				out.append(c);

			}

		}

		String output = out.toString();

		return plural
		       ? plural(output)
		       : output;
	}

	/**
	 * Test method.
	 */
	public static void main(String[] args) {

		String[] input = { "check_ins", "CheckIns", "blog_entry", "BlogEntry", "blog_entries", "BlogEntries", "blogentry", "blogentries" };

		for (int i = 0; i < input.length; i++) {

			System.out.println(StringUtils.rightPad(input[i], 20) + StringUtils.leftPad(toUpperCamelCase(input[i]), 20) + StringUtils.leftPad(toUnderscore(input[i], true), 20)
					   + StringUtils.leftPad(toUnderscore(input[i], false), 20));

		}
	}

	public static String plural(String type) {

		int len = type.length();

		if (type.substring(len - 1, len).equals("y")) {

			return type.substring(0, len - 1) + "ies";

		} else if (!(type.substring(len - 1, len).equals("s"))) {

			return type.concat("s");

		} else {
			return type;
		}
	}
}
