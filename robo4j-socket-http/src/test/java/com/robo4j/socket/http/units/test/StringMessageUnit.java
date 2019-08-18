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

package com.robo4j.socket.http.units.test;

import com.robo4j.AttributeDescriptor;
import com.robo4j.ConfigurationException;
import com.robo4j.DefaultAttributeDescriptor;
import com.robo4j.RoboContext;
import com.robo4j.RoboUnit;
import com.robo4j.configuration.Configuration;
import com.robo4j.socket.http.codec.StringMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class StringMessageUnit extends RoboUnit<StringMessage> {
    public static final String NAME = "stringMessageUnit";
    public static final String ATTR_MESSAGES_LATCH = "messagesLatch";
    public static final String ATTR_TOTAL_NUMBER_MESSAGES = "totalNumberMessages";
    public static final String ATTR_RECEIVED_MESSAGES = "getReceivedMessages";

    public static final DefaultAttributeDescriptor<CountDownLatch> DESCRIPTOR_MESSAGES_LATCH = DefaultAttributeDescriptor
            .create(CountDownLatch.class, ATTR_MESSAGES_LATCH);
    public static final DefaultAttributeDescriptor<Integer> DESCRIPTOR_MESSAGES_TOTAL = DefaultAttributeDescriptor
            .create(Integer.class, ATTR_TOTAL_NUMBER_MESSAGES);
    @SuppressWarnings("rawtypes")
    public static final DefaultAttributeDescriptor<List> DESCRIPTOR_RECEIVED_MESSAGES = DefaultAttributeDescriptor
            .create(List.class, ATTR_RECEIVED_MESSAGES);

    private volatile AtomicInteger counter;
    private List<String> receivedMessages = Collections.synchronizedList(new ArrayList<>());
    private CountDownLatch messagesLatch;

    public StringMessageUnit(RoboContext context, String id) {
        super(StringMessage.class, context, id);
        this.counter = new AtomicInteger(0);
    }

    @Override
    protected void onInitialization(Configuration configuration) throws ConfigurationException {
        int totalNumber = configuration.getInteger(ATTR_TOTAL_NUMBER_MESSAGES, 0);
        if (totalNumber > 0) {
            messagesLatch = new CountDownLatch(totalNumber);
        }
    }

    @Override
    public void onMessage(StringMessage message) {
        counter.incrementAndGet();
        receivedMessages.add(message.getMessage());
        if (messagesLatch != null) {
            messagesLatch.countDown();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <R> R onGetAttribute(AttributeDescriptor<R> attribute) {
        if (attribute.getAttributeName().equals(ATTR_TOTAL_NUMBER_MESSAGES)
                && attribute.getAttributeType() == Integer.class) {
            return (R) Integer.valueOf(counter.get());
        }
        if (attribute.getAttributeName().equals(ATTR_RECEIVED_MESSAGES)
                && attribute.getAttributeType() == List.class) {
            return (R) receivedMessages;
        }
        if (attribute.getAttributeName().equals(ATTR_MESSAGES_LATCH)
                && attribute.getAttributeType() == CountDownLatch.class) {
            return (R) messagesLatch;
        }
        return null;
    }

    @Override
    public Collection<AttributeDescriptor<?>> getKnownAttributes() {
        return Arrays.asList(DESCRIPTOR_MESSAGES_LATCH, DESCRIPTOR_MESSAGES_TOTAL, DESCRIPTOR_RECEIVED_MESSAGES);
    }
}