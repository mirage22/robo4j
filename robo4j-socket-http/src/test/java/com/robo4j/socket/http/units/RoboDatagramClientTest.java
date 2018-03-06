/*
 * Copyright (c) 2014, 2018, Marcus Hirt, Miroslav Wengner
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

package com.robo4j.socket.http.units;

import com.robo4j.RoboContext;
import com.robo4j.RoboReference;
import com.robo4j.socket.http.units.test.StringConsumer;
import com.robo4j.util.SystemUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Testing Datagram client/server decorated messages
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class RoboDatagramClientTest {
    private static final int MAX_NUMBER = 42;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void datagramClientServerTest() throws Exception {

		RoboContext producerSystem = RoboContextUtils.loadSimpleByXml("robo_datagram_client_request_producer_text.xml");
		RoboContext consumerSystem = RoboContextUtils.loadSimpleByXml("robo_datagram_client_request_consumer_text.xml");

		consumerSystem.start();
		producerSystem.start();

        System.out.println("consumer: State after start:");
        System.out.println(SystemUtil.printStateReport(consumerSystem));

        System.out.println("producer: State after start:");
        System.out.println(SystemUtil.printStateReport(producerSystem));

        RoboReference<Integer> decoratedProducer = producerSystem.getReference("decoratedProducer");
        decoratedProducer.sendMessage(MAX_NUMBER);

        RoboReference<String> stringConsumerProducer = consumerSystem.getReference(StringConsumer.NAME);
        CountDownLatch countDownLatchStringProducer = stringConsumerProducer
                .getAttribute(StringConsumer.DESCRIPTOR_COUNT_DOWN_LATCH).get(1, TimeUnit.MINUTES);

        countDownLatchStringProducer.await(1, TimeUnit.MINUTES);
        final int consumerTotalNumber = stringConsumerProducer
                .getAttribute(StringConsumer.DESCRIPTOR_MESSAGES_NUMBER_TOTAL).get(1, TimeUnit.MINUTES);

        producerSystem.shutdown();
        consumerSystem.shutdown();

        Assert.assertTrue(consumerTotalNumber == MAX_NUMBER);

	}

}