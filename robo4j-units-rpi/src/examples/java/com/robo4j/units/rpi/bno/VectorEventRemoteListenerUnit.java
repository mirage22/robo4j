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

package com.robo4j.units.rpi.bno;

import com.robo4j.ConfigurationException;
import com.robo4j.RoboContext;
import com.robo4j.RoboReference;
import com.robo4j.RoboUnit;
import com.robo4j.configuration.Configuration;
import com.robo4j.hw.rpi.imu.bno.VectorEvent;
import com.robo4j.logging.SimpleLoggingUtil;
import com.robo4j.net.LookupServiceProvider;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class VectorEventRemoteListenerUnit extends RoboUnit<VectorEvent> {
    public static final String ATTR_TARGET_CONTEXT = "targetContext";
    public static final String ATTR_REMOTE_UNIT = "remoteUnit";

    private final BlockingQueue<VectorEvent> eventQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean active = new AtomicBoolean(true);
    private String targetContext;
    private String remoteUnit;

    public VectorEventRemoteListenerUnit(RoboContext context, String id) {
        super(VectorEvent.class, context, id);
    }

    @Override
    protected void onInitialization(Configuration configuration) throws ConfigurationException {
        targetContext = configuration.getString(ATTR_TARGET_CONTEXT, null);
        remoteUnit = configuration.getString(ATTR_REMOTE_UNIT, null);
    }

    @Override
    public void start() {
        getContext().getScheduler().execute(() -> {
            while(active.get()){
                try {
                    VectorEvent message = eventQueue.take();
                    System.out.println("EMITTED MESSAGE:" + message);
                    RoboContext remoteContext = LookupServiceProvider.getDefaultLookupService().getContext(targetContext);
                    if(remoteContext != null){
                        RoboReference<VectorEvent> roboReference = remoteContext.getReference(remoteUnit);
                        if(roboReference != null){
                            roboReference.sendMessage(message);
                        }
                    } else {
                        SimpleLoggingUtil.info(getClass(), String.format("context not found: %s", targetContext));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void shutdown() {
        active.set(false);
        super.shutdown();
    }

    @Override
    public void onMessage(VectorEvent message) {
        SimpleLoggingUtil.info(getClass(), "received:" + message);
        eventQueue.add(message);
    }
}