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

package com.robo4j.hw.rpi.imu;

import com.robo4j.hw.rpi.imu.bno.DeviceListener;
import com.robo4j.hw.rpi.imu.bno.DataEvent3f;
import com.robo4j.hw.rpi.imu.bno.DeviceSensorReport;
import com.robo4j.hw.rpi.imu.impl.BNO080SPIDevice;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class BNO080SPIExample {

	public static void main(String[] args) throws Exception {

		DeviceListener listener = (DataEvent3f event) -> System.out.println("ShtpPacketResponse: " + event);

		System.out.println("BNO080 SPI Example");
		BNO080SPIDevice device = new BNO080SPIDevice();
		device.addListener(listener);
		device.start(DeviceSensorReport.ACCELEROMETER, 100);
		System.out.println("CLICK TO END...");
		System.in.read();
		device.shutdown();

	}
}
