using System.Net;
using System.Text;
using Newtonsoft.Json;
using System.IO;
using System.Diagnostics;
using System;
using OpenHardwareMonitor.Hardware;

public class DataHttpRequestHandler : HttpRequestHandler {
    public const string NAME = "/data";

    private Computer computer;
    private UpdateVisitor updateVisitor;

    public DataHttpRequestHandler(Computer computer, UpdateVisitor updateVisitor) {
        this.computer = computer;
        this.updateVisitor = updateVisitor;
    }

    public void Handle(HttpListenerContext context) {

        HttpListenerResponse response = context.Response;
        bool success = false;

        // Get name from query string
        string type = context.Request.QueryString["type"];
        string message = "";

        // Check what kind of data is requested
        if (type != null) {
            switch (type) {
                case "cpu": message = handleCPU(); success = true; break;
                case "gpu": message = handleGPU(); success = true; break;
                case "disks": message = handleDisks(); success = true; break;
                case "processes": message = handleProcesses(context.Request.QueryString["sort"]); success = true; break;
                case "system": message = handleSystem(); success = true; break;
            }
        } else {
            response.StatusCode = (int) HttpStatusCode.BadRequest;
        }

        if (success) { // Fill in response body
            response.StatusCode = (int) HttpStatusCode.OK;
            byte[] messageBytes = Encoding.Default.GetBytes(message);
            response.OutputStream.Write(messageBytes, 0, messageBytes.Length);
            response.ContentType = "application/json";
        }

        // Send the HTTP response to the client        
        response.Close();
    }

    public string GetName() {
        return NAME;
    }

    private String handleCPU() {
        // Refresh
        computer.Accept(updateVisitor);

        // Generate JSON
        return JsonConvert.SerializeObject(new JSONCPUData(computer));
    }

    private String handleGPU() {
        // Refresh
        computer.Accept(updateVisitor);

        // Generate JSON
        return JsonConvert.SerializeObject(new JSONGPUData(computer));
    }

    private String handleDisks() {
        // For each drive create a JSON object
        JSONDiskData[] DataTemp = new JSONDiskData[DriveInfo.GetDrives().Length];
        int i = 0;
        foreach (DriveInfo Drive in DriveInfo.GetDrives()) {
            if (Drive.IsReady) {
                JSONDiskData Data_single = new JSONDiskData(Drive);
                DataTemp[i] = Data_single;
                i++;
            }
        }

        // Add data to an array
        JSONDiskData[] Data = new JSONDiskData[i];
        i = 0;
        foreach (JSONDiskData JSON in DataTemp) {
            if (JSON != null) {
                Data[i] = JSON;
                i++;
            }
        }

        // Generate JSON
        return JsonConvert.SerializeObject(Data);
    }

    private String handleProcesses(String sort) {
        Process[] all = Process.GetProcesses();

        if (sort == "name") {
            return JsonConvert.SerializeObject(new JSONProcessData(all, JSONProcessDataSort.SORT_BY_NAME));
        } else {
            return JsonConvert.SerializeObject(new JSONProcessData(all, JSONProcessDataSort.SORT_BY_USAGE));
        }
    }

    private String handleSystem() {
        // Refresh
        computer.Accept(updateVisitor);

        // Generate JSON
        return JsonConvert.SerializeObject(new JSONSystemData(computer));
    }

    /**
    private String handleSystem(String sub) {
        // Refresh
        computer.Accept(updateVisitor);

        // Find superIO chip
        IHardware superIO = null;

        foreach (IHardware hardware in computer.Hardware) {
            if (hardware.HardwareType == HardwareType.Mainboard) {
                superIO = hardware.SubHardware[0];
            }
        }

        switch (sub) {
            case "voltages": return handleVoltages(superIO);
            case "temps": return handleTemps(superIO);
            case "fans": return handleFans(superIO);
            case "general": return handleGeneral();
            default: return "";
        }
    }

    private String handleVoltages(IHardware superIO) {
        // Find how much data we have
        int i = 0;

        foreach (ISensor sensor in superIO.Sensors) {
            if (sensor.SensorType == SensorType.Voltage) {
                i++;
            }
        }

        // Load data
        JSONNameValue[] Voltages = new JSONNameValue[i];

        i = 0;

        foreach (ISensor sensor in superIO.Sensors) {
            if (sensor.SensorType == SensorType.Voltage) {
                Voltages[i] = new JSONNameValueFloat(sensor.Name, (float) sensor.Value, 3);
                i++;
            }
        }

        // Generate JSON
        return JsonConvert.SerializeObject(Voltages);
    }

    private String handleFans(IHardware superIO) {
        // Find how much data we have
        int i = 0;

        foreach (ISensor sensor in superIO.Sensors) {
            if (sensor.SensorType == SensorType.Fan) {
                i++;
            }
        }

        // Load data
        JSONNameValue[] Fans = new JSONNameValue[i];

        i = 0;

        foreach (ISensor sensor in superIO.Sensors) {
            if (sensor.SensorType == SensorType.Fan) {
                Fans[i] = new JSONNameValueInt(sensor.Name, (int) sensor.Value);
                i++;
            }
        }

        // Generate JSON
        return JsonConvert.SerializeObject(Fans);
    }

    private String handleTemps(IHardware superIO) {
        // Find how much data we have
        int i = 0;

        foreach (ISensor sensor in superIO.Sensors) {
            if (sensor.SensorType == SensorType.Temperature) {
                i++;
            }
        }

        // Load data
        JSONNameValue[] Temps = new JSONNameValue[i];

        i = 0;

        foreach (ISensor sensor in superIO.Sensors) {
            if (sensor.SensorType == SensorType.Temperature) {
                Temps[i] = new JSONNameValueFloat(sensor.Name, (float) sensor.Value, 1);
                i++;
            }
        }

        // Generate JSON
        return JsonConvert.SerializeObject(Temps);
    }

    private String handleGeneral() {
        String motherboard = "";
        String cpu = "";

        foreach (IHardware hardware in computer.Hardware) {
            switch (hardware.HardwareType) {
                case HardwareType.Mainboard: motherboard = hardware.Name; break;
                case HardwareType.CPU: cpu = hardware.Name; break;
            } 
        }

        return JsonConvert.SerializeObject(new JSONGeneralInfo(motherboard, cpu));
    }
     */
}