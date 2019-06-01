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

import com.robo4j.hw.rpi.imu.impl.BNO080SPIDevice;

/**
 * SparkFun BNO080 QWIIC VR IMU
 * RotationVector example
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class BNO080Example {

    public static void main(String[] args) throws Exception {
        System.out.println("BNO080 Example");
        BNO080SPIDevice device = new BNO080SPIDevice();
        if(device.configureSpiPins()){
            if(!device.beginSPI()){
                System.out.println("BNO080 over SPI not detected. Are you sure you have all 6 connections? Freezing...");
            } else {
                device.enableRotationVector(50);
                System.out.println("Rotation vector enabled");
                System.out.println("Output in form i, j, k, real, accuracy");
                device.startRotationVector(25, 3);
                System.out.println("BNO080 DONE");
            }
        } else {
            System.out.println("BNO080 not configured");
        }
    }
}