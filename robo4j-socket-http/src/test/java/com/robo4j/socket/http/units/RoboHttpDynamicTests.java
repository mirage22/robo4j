/*
 * Copyright (C) 2014, 2017. Miroslav Wengner, Marcus Hirt
 * This RoboHttpDynamicTests.java  is part of robo4j.
 * module: robo4j-core
 *
 * robo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * robo4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with robo4j .  If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.socket.http.units;

import com.robo4j.core.DefaultAttributeDescriptor;
import com.robo4j.core.LifecycleState;
import com.robo4j.core.RoboBuilder;
import com.robo4j.core.RoboContext;
import com.robo4j.core.RoboReference;
import com.robo4j.core.StringConsumer;
import com.robo4j.core.configuration.Configuration;
import com.robo4j.core.configuration.ConfigurationFactory;
import com.robo4j.core.util.SystemUtil;
import com.robo4j.socket.http.HttpMethod;
import com.robo4j.socket.http.units.test.HttpCommandTestController;
import com.robo4j.socket.http.util.JsonUtil;
import com.robo4j.socket.http.util.RoboHttpUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.Future;

/**
 *
 * Dynamic HttpUnit request/method configuration
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class RoboHttpDynamicTests {

	private static final String ID_HTTP_SERVER = "http";
	private static final int PORT = 8025;
	private static final String ID_CLIENT_UNIT = "httpClient";
	private static final String ID_TARGET_UNIT = "controller";
	private static final int MESSAGES_NUMBER = 6;
	private static final String HOST_SYSTEM = "0.0.0.0";
	private static final String REQUEST_CONSUMER = "request_consumer";
	private static final String HTTP_METHOD = "POST";
	static final String JSON_STRING = "{\"value\":\"move\"}";

	/**
	 * Motivation Client system is sending messages to the main system over HTTP
	 * Main System receives desired number of messages.
	 *
	 * Values are requested by Attributes
	 * 
	 * @throws Exception
	 */
	@Test
	public void simpleHttpNonUnitTest() throws Exception {

		/* tested system configuration */
		RoboContext mainSystem = getServerRoboSystem();
		System.out.println(SystemUtil.printStateReport(mainSystem));
		System.out.println("Server start after start:");

		/* system which is testing main system */
		RoboContext clientSystem = getClientRoboSystem();
		RoboReference<Object> httpClientReference = clientSystem.getReference(ID_CLIENT_UNIT);

		System.out.println(SystemUtil.printStateReport(clientSystem));
		System.out.println("Client State after start:");

		System.out.println("State after start:");
		System.out.println(SystemUtil.printStateReport(mainSystem));

		/* client system sending a messages to the main system */
		for (int i = 0; i < MESSAGES_NUMBER; i++) {
			httpClientReference.sendMessage(
					RoboHttpUtils.createRequest(HttpMethod.POST, HOST_SYSTEM, "/".concat(ID_TARGET_UNIT), JSON_STRING));
		}

		Thread.sleep(100);

		clientSystem.stop();
		clientSystem.shutdown();

		System.out.println("Going Down!");

		final DefaultAttributeDescriptor<Integer> descriptor = DefaultAttributeDescriptor.create(Integer.class,
				StringConsumer.PROP_GET_NUMBER_OF_SENT_MESSAGES);
		final Future<Integer> messagesFuture = mainSystem.getReference(REQUEST_CONSUMER).getAttribute(descriptor);
		final int receivedMessages = messagesFuture.get();

		mainSystem.stop();
		mainSystem.shutdown();

		System.out.println("System is Down!");
		Assert.assertNotNull(mainSystem.getUnits());
		Assert.assertEquals("wrong received messages", receivedMessages, MESSAGES_NUMBER);
	}

	// Private Methods
	private RoboContext getServerRoboSystem() throws Exception {
		/* tested system configuration */
		RoboBuilder builder = new RoboBuilder();

		Configuration config = ConfigurationFactory.createEmptyConfiguration();
		config.setString("target", ID_TARGET_UNIT);
		config.setInteger("port", PORT);
		config.setString("packages", "com.robo4j.socket.http.units.test.codec");
		// FIXME: 06.09.17 (miro) -> fix stopper codecs
		config.setInteger("stopper", -1);
		config.setString(RoboHttpUtils.HTTP_TARGET_UNITS,
				JsonUtil.getJsonByMap(Collections.singletonMap(ID_TARGET_UNIT, HTTP_METHOD)));
		builder.add(HttpServerUnit.class, config, ID_HTTP_SERVER);

		config = ConfigurationFactory.createEmptyConfiguration();
		config.setString("target", REQUEST_CONSUMER);
		builder.add(HttpCommandTestController.class, config, ID_TARGET_UNIT);

		builder.add(StringConsumer.class, REQUEST_CONSUMER);

		RoboContext result = builder.build();
		Assert.assertNotNull(result.getUnits());
		Assert.assertEquals(result.getUnits().size(), 3);
		Assert.assertEquals(result.getReference(ID_HTTP_SERVER).getState(), LifecycleState.INITIALIZED);
		Assert.assertEquals(result.getState(), LifecycleState.INITIALIZED);

		result.start();
		System.out.println(SystemUtil.printSocketEndPoint(result.getReference(ID_HTTP_SERVER),
				result.getReference(ID_TARGET_UNIT)));
		return result;
	}

	private RoboContext getClientRoboSystem() throws Exception {
		/* system which is testing main system */
		RoboBuilder builder = new RoboBuilder();

		Configuration config = ConfigurationFactory.createEmptyConfiguration();
		config.setString("address", HOST_SYSTEM);
		config.setInteger("port", PORT);
		/* specific configuration */
		config.setString(RoboHttpUtils.HTTP_TARGET_UNITS,
				JsonUtil.getJsonByMap(Collections.singletonMap(ID_TARGET_UNIT, HTTP_METHOD)));
		builder.add(HttpClientUnit.class, config, ID_CLIENT_UNIT);

		RoboContext result = builder.build();
		result.start();
		System.out.println("Client State after start:");
		System.out.println(SystemUtil.printStateReport(result));
		return result;
	}
}