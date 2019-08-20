/*
 * Copyright (c) 2014, 2019, Marcus Hirt, Miroslav Wengner
 *
 * Robo4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Robo4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */
package com.robo4j.socket.http.util;

import static com.robo4j.socket.http.util.HttpConstant.HTTP_NEW_LINE;
import static com.robo4j.socket.http.util.HttpConstant.HTTP_NEW_LINE_CURR;

/**
 * Util class for http message
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public final class HttpMessageUtils {

	public static final String SPACE = "\u0020";
	@Deprecated
	public static final String HTTP_HEADER_BODY_DELIMITER = HTTP_NEW_LINE.concat(HTTP_NEW_LINE);
	public static final int HTTP_HEADER_BODY_DELIMITER_SIZE = 4;
	public static final String HTTP_HEADER_BODY_DELIMITER_CURR = HTTP_NEW_LINE_CURR.concat(HTTP_NEW_LINE_CURR);
	public static final String COLON = ":";
	public static final int HTTP_DEFAULT_PORT = 80;

	/**
	 * The HTTP separator characters. Defined in RFC 2616, section 2.2
	 */
	private static final String HTTP_SEPARATORS = " ,;=()<>@:\\\"/[]?{}\t";

	public static final int POSITION_HEADER = 0, POSITION_BODY = 1;
	public static final int METHOD_KEY_POSITION = 0, URI_VALUE_POSITION = 1, VERSION_POSITION = 2, HTTP_HEADER_SEP = 9;

	public static String getHttpSeparator(int position) {
		return Character.toString(HTTP_SEPARATORS.charAt(position));
	}
}
