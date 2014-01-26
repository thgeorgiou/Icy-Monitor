using System;
using OpenHardwareMonitor.Hardware;

public class JSONDiskData {
	public String Name;
	public float Temp;
	
	
	public JSONDiskData(String Name, float Temp) {
		this.Name = Name;
        this.Temp = Temp;
	}
}