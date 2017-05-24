/*
 * Copyright (c) 2014, 2017, Marcus Hirt, Miroslav Wengner
 *
 * Robo4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Robo4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.core.util;

import java.util.Map;
import java.util.stream.Collectors;

import com.robo4j.core.httpunit.Constants;

/**
 *
 * Simple Json util
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public final class JsonUtil {

	public static String getJsonByMap(Map<String, Object> map) {
		StringBuilder sb = new StringBuilder(Constants.UTF8_CURLY_BRACKET_LEFT);

		sb.append(map.entrySet().stream()
				.map(e -> new StringBuilder(Constants.UTF8_QUOTATION_MARK).append(e.getKey())
						.append(Constants.UTF8_QUOTATION_MARK).append(Constants.UTF8_COLON)
						.append(Constants.UTF8_QUOTATION_MARK).append(e.getValue())
						.append(Constants.UTF8_QUOTATION_MARK).toString())
				.collect(Collectors.joining(Constants.UTF8_COMMA))).append(Constants.UTF8_CURLY_BRACKET_RIGHT);
		return sb.toString();
	}

}