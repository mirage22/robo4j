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

package com.robo4j.socket.http.request;

import com.robo4j.AttributeDescriptor;
import com.robo4j.ConfigurationException;
import com.robo4j.LifecycleState;
import com.robo4j.RoboContext;
import com.robo4j.RoboReference;
import com.robo4j.socket.http.HttpMethod;
import com.robo4j.socket.http.dto.ResponseUnitDTO;
import com.robo4j.socket.http.enums.StatusCode;
import com.robo4j.socket.http.message.HttpDecoratedRequest;
import com.robo4j.socket.http.units.CodecRegistry;
import com.robo4j.socket.http.units.HttpServerUnit;
import com.robo4j.socket.http.units.PathHttpMethod;
import com.robo4j.socket.http.units.ServerContext;
import com.robo4j.socket.http.units.ServerPathConfig;
import com.robo4j.socket.http.util.ChannelRequestBuffer;
import com.robo4j.socket.http.util.CodeRegistryUtils;
import com.robo4j.socket.http.util.HttpPathConfigJsonBuilder;
import com.robo4j.socket.http.util.JsonUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.robo4j.socket.http.codec.AbstractHttpMessageCodec.DEFAULT_PACKAGE;
import static com.robo4j.socket.http.util.HttpPathUtils.DEFAULT_GET_SERVER_METHOD;
import static com.robo4j.socket.http.util.HttpPathUtils.DEFAULT_GET_SERVER_PATH_CONFIG;
import static com.robo4j.socket.http.util.TestUtils.getResourcePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
class RoboRequestCallableTest {

	@Test
	void notInitiatedServerContextGetDefaultRequestBadRequestResponseTest() throws Exception {

		HttpResponseProcess expectedResponse = new HttpResponseProcess(null, null, null, StatusCode.BAD_REQUEST, null);
		RoboContext roboContext = mock(RoboContext.class);
		ServerContext serverContext = mock(ServerContext.class);

		final Path path = getResourcePath("httpGetRequestDefaultPath.txt");

		try (FileChannel fileChannel = FileChannel.open(path)) {
			HttpDecoratedRequest request = getDecoratedRequestByFileChannel(fileChannel);
			RoboRequestFactory factory = initRoboRequestFactory();

			RoboRequestCallable callable = new RoboRequestCallable(roboContext, serverContext, request, factory);
			HttpResponseProcess process = callable.call();

			assertEquals(expectedResponse, process);

		}

	}

	@Test
	void initiatedServerContextDefaultGetRequestOkResponseTest() throws Exception {

		Map<String, LifecycleState> map = new LinkedHashMap<>();
		map.put("roboSystem1", LifecycleState.STARTED);
		map.put("http_server", LifecycleState.STARTED);
		map.put("sensor_light", LifecycleState.SHUTDOWN);
		map.put("sensor_sound", LifecycleState.STOPPED);

		String expectedResultResponse = createResponseUnitDTOs(map);

		HttpResponseProcess expectedResponse = new HttpResponseProcess("/", null, HttpMethod.GET, StatusCode.OK,
				expectedResultResponse);

		RoboContext roboContext = getMockedRoboContext("roboSystem1", LifecycleState.STARTED);
		RoboReference<?> httpServerUnit = getMockedRoboReference("http_server", LifecycleState.STARTED);
		RoboReference<?> sensorLightUnit = getMockedRoboReference("sensor_light", LifecycleState.SHUTDOWN);
		RoboReference<?> sensorSoundUnit = getMockedRoboReference("sensor_sound", LifecycleState.STOPPED);
		Collection<RoboReference<?>> units = Arrays.asList(httpServerUnit, sensorLightUnit, sensorSoundUnit);
		when(roboContext.getUnits()).thenReturn(units);
		ServerContext serverContext = mock(ServerContext.class);
		when(serverContext.getPathConfig(DEFAULT_GET_SERVER_METHOD)).thenReturn(DEFAULT_GET_SERVER_PATH_CONFIG);

		final Path path = getResourcePath("httpGetRequestDefaultPath.txt");

		try (FileChannel fileChannel = FileChannel.open(path)) {
			HttpDecoratedRequest request = getDecoratedRequestByFileChannel(fileChannel);
			RoboRequestFactory factory = initRoboRequestFactory();

			RoboRequestCallable callable = new RoboRequestCallable(roboContext, serverContext, request, factory);
			HttpResponseProcess process = callable.call();

			assertEquals(expectedResponse, process);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	void initiatedServerContextGetRequestToSpecificUnitNoAttributesOkResponseTest() throws Exception {

		String observedRoboUnitName = "http_server";
		String pathConfig = "/units/" + observedRoboUnitName;
		//@formatter:off
		HttpResponseProcess expectedResponse = new HttpResponseProcess(pathConfig, observedRoboUnitName, HttpMethod.GET,
				StatusCode.OK, "[" +
				"{\"id\":\"address\",\"type\":\"java.lang.String\",\"value\":\"127.0.0.1\"}," +
				"{\"id\":\"serverPort\",\"type\":\"java.lang.Integer\",\"value\":\"8061\"}," +
				"{\"id\":\"paths\",\"type\":\"java.util.ArrayList\",\"value\":" +
					"[{\"roboUnit\":\"http_server\",\"method\":\"GET\",\"callbacks\":{\"\":{}}}]" +
				"}]");
		//@formatter:on

		RoboContext roboContext = getMockedRoboContext("roboSystem1", LifecycleState.STARTED);
		RoboReference<Object> httpServerUnit = (RoboReference<Object>) getMockedRoboReference(observedRoboUnitName,
				LifecycleState.STARTED);
		RoboReference<?> sensorLightUnit = getMockedRoboReference("sensor_light", LifecycleState.SHUTDOWN);
		Collection<RoboReference<?>> units = Arrays.asList(httpServerUnit, sensorLightUnit);
		when(roboContext.getUnits()).thenReturn(units);

		String pathsString = HttpPathConfigJsonBuilder.Builder().addPath(observedRoboUnitName, HttpMethod.GET).build();

		when(httpServerUnit.getKnownAttributes()).thenReturn(
				Arrays.asList(HttpServerUnit.DESCRIPTOR_ADDRESS,
						HttpServerUnit.DESCRIPTOR_PORT,
						HttpServerUnit.DESCRIPTOR_PATHS));

		mockServerUnitAttribute(httpServerUnit, HttpServerUnit.DESCRIPTOR_ADDRESS, "127.0.0.1");
		mockServerUnitAttribute(httpServerUnit, HttpServerUnit.DESCRIPTOR_PORT, 8061);
		mockServerUnitAttribute(httpServerUnit, HttpServerUnit.DESCRIPTOR_PATHS, pathsString);

		when(httpServerUnit.getMessageType()).thenReturn(Object.class);
		ServerContext serverContext = mock(ServerContext.class);
		when(serverContext.getPathConfig(new PathHttpMethod(pathConfig, HttpMethod.GET)))
				.thenReturn(new ServerPathConfig(pathConfig, httpServerUnit, HttpMethod.GET));

		final Path path = getResourcePath("httpGetRequestPathUnitsSpecific.txt");

		try (FileChannel fileChannel = FileChannel.open(path)) {
			HttpDecoratedRequest request = getDecoratedRequestByFileChannel(fileChannel);
			RoboRequestFactory factory = initRoboRequestFactory();

			RoboRequestCallable callable = new RoboRequestCallable(roboContext, serverContext, request, factory);
			HttpResponseProcess process = callable.call();

			assertEquals(expectedResponse, process);
		}
	}

	@SuppressWarnings("unchecked")
	private void mockServerUnitAttribute(RoboReference<Object> httpServerUnit, AttributeDescriptor<?> descriptor,
			Object value) throws Exception {
		final Future<Object> mockedFutureAddress = mock(Future.class);
		when(httpServerUnit.getAttribute((AttributeDescriptor<Object>)descriptor)).thenReturn(mockedFutureAddress);
		when(mockedFutureAddress.get()).thenReturn(value);
	}

	private String createResponseUnitDTOs(Map<String, LifecycleState> unitsMap) {
		List<ResponseUnitDTO> list = new LinkedList<>();
		for (Map.Entry<String, LifecycleState> entry : unitsMap.entrySet()) {
			list.add(new ResponseUnitDTO(entry.getKey(), entry.getValue()));
		}
		return JsonUtil.toJsonArray(list);
	}

	private RoboReference<?> getMockedRoboReference(String id, LifecycleState state) {
		RoboReference<?> roboReference = mock(RoboReference.class);
		when(roboReference.getId()).thenReturn(id);
		when(roboReference.getState()).thenReturn(state);
		return roboReference;
	}

	private RoboContext getMockedRoboContext(String id, LifecycleState state) {
		RoboContext roboContext = mock(RoboContext.class);
		when(roboContext.getId()).thenReturn(id);
		when(roboContext.getState()).thenReturn(state);
		return roboContext;
	}

	private HttpDecoratedRequest getDecoratedRequestByFileChannel(FileChannel fileChannel) throws IOException {
		ChannelRequestBuffer channelRequestBuffer = new ChannelRequestBuffer();
		return channelRequestBuffer.getHttpDecoratedRequestByChannel(fileChannel);
	}

	private RoboRequestFactory initRoboRequestFactory() throws ConfigurationException {
		CodecRegistry codecRegistry = CodeRegistryUtils.getCodecRegistry(DEFAULT_PACKAGE);
		return new RoboRequestFactory(codecRegistry);
	}

}