using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

class JSONDevicesList {
    public readonly List<JSONDevice> System;
    public readonly List<JSONDevice> CPU;
    public readonly List<JSONDevice> GPU;

    public JSONDevicesList() {
        System = new List<JSONDevice>();
        CPU = new List<JSONDevice>();
        GPU = new List<JSONDevice>();
    }

    public void AddDevice(JSONDeviceCategory cat, String name, String type) {
        JSONDevice device = new JSONDevice(name, type);

        switch (cat) {
            case JSONDeviceCategory.System: System.Add(device); break;
            case JSONDeviceCategory.CPU: CPU.Add(device); break;
            case JSONDeviceCategory.GPU: GPU.Add(device); break;
        }
    }
}

class JSONDevice {
    public String Name;
    public String Type;

    public JSONDevice(String name, String type) {
        this.Name = name;
        this.Type = type;
    }
}

enum JSONDeviceCategory {
    System, CPU, GPU, Null
}