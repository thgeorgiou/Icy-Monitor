using System;
using OpenHardwareMonitor.Hardware;

class JSONSystemData {
    public JSONNameValue[] Voltages, Temp, Fans;

    public JSONSystemData(Computer computer) {
        // Find the superIO chip.
        IHardware superIO = null;

        foreach (IHardware hardware in computer.Hardware) {
            if (hardware.HardwareType == HardwareType.Mainboard) {
                try {
                    superIO = hardware.SubHardware[0];
                } catch (Exception e) {
                    Console.WriteLine(e.Message);
                    superIO = null;
                }
            }
        }

        // If we have a superIO chip
        if (superIO != null) {
            // Find how much data we have
            int iVoltages = 0;
            int iFans = 0;
            int iTemp = 0;

            foreach (ISensor sensor in superIO.Sensors) {
                switch (sensor.SensorType) {
                    case SensorType.Voltage: iVoltages++; break;
                    case SensorType.Fan: iFans++; break;
                    case SensorType.Temperature: iTemp++; break;
                }
            }

            // Load data
            Voltages = new JSONNameValue[iVoltages];
            Fans = new JSONNameValue[iFans];
            Temp = new JSONNameValue[iTemp];

            iVoltages = 0;
            iFans = 0;
            iTemp = 0;

            foreach (ISensor sensor in superIO.Sensors) {
                switch (sensor.SensorType) {
                    case SensorType.Voltage: Voltages[iVoltages] = new JSONNameValueFloat(sensor.Name, (float) sensor.Value, (float) sensor.Min, (float) sensor.Max, 3); iVoltages++; break;
                    case SensorType.Fan: Fans[iFans] = new JSONNameValueInt(sensor.Name, (int) sensor.Value, (int) sensor.Min, (int) sensor.Max); iFans++; break;
                    case SensorType.Temperature: Temp[iTemp] = new JSONNameValueFloat(sensor.Name, (float) sensor.Value, (float) sensor.Min, (float) sensor.Max, 1); iTemp++; break;
                }
            }
        } else { // If we don't have a superIO chip. Happens with some cheap MBs or with some laptops.
            Voltages = new JSONNameValue[0];
            Fans = new JSONNameValue[0];
            Temp = new JSONNameValue[0];
        }
    }
}