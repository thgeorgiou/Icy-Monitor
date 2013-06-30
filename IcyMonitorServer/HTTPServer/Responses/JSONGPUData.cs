using System;
using OpenHardwareMonitor.Hardware;

class JSONGPUData {
    public JSONNameValue[] Temp, Fans, Load, Clock;

    public JSONGPUData(Computer computer) {
        int iTemp = 0;
        int iFans = 0;
        int iLoad = 0;
        int iClock = 0;

        // Get data
        foreach (IHardware hardware in computer.Hardware) {
            if (hardware.HardwareType == HardwareType.GpuAti || hardware.HardwareType == HardwareType.GpuNvidia) {
                foreach (ISensor sensor in hardware.Sensors) {
                    switch (sensor.SensorType) {
                        case SensorType.Clock: iClock++; break;
                        case SensorType.Temperature: iTemp++; break;
                        case SensorType.Load: iLoad++; break;
                        case SensorType.Fan: iFans++;  break;
                    }
                }
            }
        }

        Temp = new JSONNameValueFloat[iTemp];
        Fans = new JSONNameValueInt[iFans];
        Load = new JSONNameValueInt[iLoad];
        Clock = new JSONNameValueInt[iClock];

        int gpu = 0;
        iTemp = 0;
        iClock = 0;
        iFans = 0;
        iLoad = 0;
        foreach (IHardware hardware in computer.Hardware) {
            if (hardware.HardwareType == HardwareType.GpuAti || hardware.HardwareType == HardwareType.GpuNvidia) {
                foreach (ISensor sensor in hardware.Sensors) {
                    switch (sensor.SensorType) {
                        case SensorType.Clock:
                            Clock[iClock] = new JSONNameValueInt(sensor.Name + " (" + (gpu + 1) + ")", (int) sensor.Value);
                            iClock++; 
                            break;
                        case SensorType.Temperature:
                            Temp[iTemp] = new JSONNameValueFloat(sensor.Name + " (" + (gpu + 1) + ")", (float) sensor.Value);
                            iTemp++; 
                            break;
                        case SensorType.Load:
                            Load[iLoad] = new JSONNameValueInt(sensor.Name + " (" + (gpu + 1) + ")", (int) sensor.Value);
                            iLoad++; 
                            break;
                        case SensorType.Fan:
                            Fans[iFans] = new JSONNameValueInt(sensor.Name + " (" + (gpu + 1) + ")", (int) sensor.Value);
                            iFans++; 
                            break;
                    }
                }
                gpu++;
            }
        }
    }    
}