/*
 * Copyright (C)  2016. Miroslav Kopecky
 * This LegoSensorWrapper.java  is part of robo4j.
 *
 *  robo4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  robo4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with robo4j .  If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.lego.sensor;

import com.robo4j.commons.sensor.GenericSensor;
import com.robo4j.lego.control.LegoSensor;
import com.robo4j.lego.enums.LegoEnginePartEnum;
import com.robo4j.lego.enums.LegoSensorEnum;
import com.robo4j.lego.enums.LegoSensorPortEnum;
import lejos.hardware.sensor.BaseSensor;

/**
 * @author Miro Kopecky (@miragemiko)
 * @since 26.11.2016
 */
public abstract class LegoSensorWrapper <SensorType extends BaseSensor> implements GenericSensor, LegoSensor {

    protected SensorType unit;
    protected LegoSensorPortEnum port;
    protected LegoSensorEnum sensor;
    protected LegoEnginePartEnum part;

    public abstract LegoSensorPortEnum getPort();
    public abstract LegoSensorEnum getSensor();
    public abstract LegoEnginePartEnum getPart();


    public SensorType getUnit() {
        return unit;
    }

    public void setUnit(SensorType unit) {
        this.unit = unit;
    }
}