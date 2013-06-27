using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using OpenHardwareMonitor.Hardware;

class JSONCPUData {
    public JSONNameValueInt[] Temp, Load, Clock;
    public JSONNameValueFloat[] Power;

    public JSONCPUData(Computer computer) {
        IHardware cpu = null;

        // Find CPU
        foreach (IHardware hardware in computer.Hardware) {
            if (hardware.HardwareType == HardwareType.CPU) {
                cpu = hardware;
            }
        }

        // Check how many data we have
        int iLoad = 0;
        int iTemp = 0;
        int iPower = 0;
        int iClock = 0;
        foreach (ISensor sensor in cpu.Sensors) {
            switch (sensor.SensorType) {
                case SensorType.Load: iLoad++; break;
                case SensorType.Temperature: iTemp++; break;
                case SensorType.Power: iPower++; break;
                case SensorType.Clock: iClock++; break;
            }
        }

        // Initialize arrays
        Load = new JSONNameValueInt[iLoad];
        Temp = new JSONNameValueInt[iTemp];
        Power = new JSONNameValueFloat[iPower];
        Clock = new JSONNameValueInt[iClock];

        // Fill arrays
        iLoad = 0;
        iTemp = 0;
        iPower = 0;
        iClock = 0;

        foreach (ISensor sensor in cpu.Sensors) {
            switch (sensor.SensorType) {
                case SensorType.Load:
                    Load[iLoad] = new JSONNameValueInt(sensor.Name, (int) sensor.Value, (int) sensor.Min, (int) sensor.Max);
                    iLoad++;
                    break;
                case SensorType.Temperature:
                    Temp[iTemp] = new JSONNameValueInt(sensor.Name, (int) sensor.Value, (int) sensor.Min, (int) sensor.Max);
                    iTemp++;
                    break;
                case SensorType.Power:
                    Power[iPower] = new JSONNameValueFloat(sensor.Name, (float) sensor.Value, (float) sensor.Min, (float) sensor.Max, 2);
                    iPower++;
                    break;
                case SensorType.Clock:
                    Clock[iClock] = new JSONNameValueInt(sensor.Name, (int) sensor.Value, (int) sensor.Min, (int) sensor.Max);
                    iClock++;
                    break;
            }
        }
    }
}