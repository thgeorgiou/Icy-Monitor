using System;
using OpenHardwareMonitor.Hardware;
using System.Linq;
using System.Collections.Generic;

class JSONSystemData {
    public List<JSONNameValue> Voltages, Temp, Fans;

    public JSONSystemData(Computer computer, bool names) {
        // Create lists
        Voltages = new List<JSONNameValue>();
        Temp = new List<JSONNameValue>();
        Fans = new List<JSONNameValue>();

        // Find the superIO chip.
        IHardware motherboard = computer.Hardware.FirstOrDefault(h => h.HardwareType == HardwareType.Mainboard && h.SubHardware.Length > 0);
        IHardware superIO;

        if (motherboard != null) {
            superIO = motherboard.SubHardware[0];

            foreach (ISensor s in superIO.Sensors) {
                String name;
                if (names) name = s.Name;
                else name = "";

                switch (s.SensorType) {
                    case SensorType.Voltage: Voltages.Add(new JSONNameValueFloat(name, (float)s.Value, 3)); break;
                    case SensorType.Fan: Fans.Add(new JSONNameValueInt(name, (int)s.Value)); break;
                    case SensorType.Temperature: Temp.Add(new JSONNameValueFloat(name, (float)s.Value, 1)); break;
                }
            }
        }
    }
}