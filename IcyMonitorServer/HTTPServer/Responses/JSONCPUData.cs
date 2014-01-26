using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using OpenHardwareMonitor.Hardware;

class JSONCPUData {
    public List<JSONNameValueInt> Load, Temp, Clock;
    public List<JSONNameValueFloat> Power;

    public JSONCPUData(Computer computer, bool names) {
        // Find CPU
        IHardware cpu = computer.Hardware.First(h => h.HardwareType == HardwareType.CPU);

        // Create lists
        Load = new List<JSONNameValueInt>();
        Temp = new List<JSONNameValueInt>();
        Clock = new List<JSONNameValueInt>();
        Power = new List<JSONNameValueFloat>();

        // Add data to lists
        if (cpu != null) {
            foreach (ISensor sensor in cpu.Sensors) {
                String name;
                if (names) name = sensor.Name;
                else name = "";

                switch (sensor.SensorType) {
                    case SensorType.Load: Load.Add(new JSONNameValueInt(name, (int)sensor.Value)); break;
                    case SensorType.Temperature: Temp.Add(new JSONNameValueInt(name, (int)sensor.Value)); break;
                    case SensorType.Clock: Clock.Add(new JSONNameValueInt(name, (int)sensor.Value)); break;
                    case SensorType.Power: Power.Add(new JSONNameValueFloat(name, (float)sensor.Value, 2)); break;
                }
            }
        }
    }
}