using System;
using OpenHardwareMonitor.Hardware;
using System.Collections.Generic;
using System.Linq;

class JSONGPUData {
    public List<JSONNameValue> Temp, Fans, Load, Clock;

    public JSONGPUData(Computer computer, bool names) {
        // Create lists
        Temp = new List<JSONNameValue>();
        Fans = new List<JSONNameValue>();
        Load = new List<JSONNameValue>();
        Clock = new List<JSONNameValue>();

        IEnumerable<IHardware> gpus = computer.Hardware.Where(h => h.HardwareType == HardwareType.GpuNvidia || h.HardwareType == HardwareType.GpuAti);

        foreach (IHardware h in gpus) {
            foreach (ISensor s in h.Sensors) {
                String name;
                if (names) name = s.Name;
                else name = "";

                switch (s.SensorType) {
                    case SensorType.Temperature: Temp.Add(new JSONNameValueFloat(name, (float)s.Value)); break;
                    case SensorType.Fan: Fans.Add(new JSONNameValueInt(name, (int)s.Value)); break;
                    case SensorType.Load: Load.Add(new JSONNameValueInt(name, (int)s.Value)); break;
                    case SensorType.Clock: Clock.Add(new JSONNameValueInt(name, (int)s.Value)); break;
                }
            }
        }
    }    
}