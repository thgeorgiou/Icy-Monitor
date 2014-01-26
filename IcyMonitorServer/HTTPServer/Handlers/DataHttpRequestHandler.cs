using System.Net;
using System.Text;
using Newtonsoft.Json;
using System.IO;
using System.Diagnostics;
using System;
using OpenHardwareMonitor.Hardware;
using System.Collections.Generic;
using System.Linq;

public class DataHttpRequestHandler : HttpRequestHandler {
    public const string NAME = "/data";

    private TrayApplicationContext _parent;

    public DataHttpRequestHandler(TrayApplicationContext parent) {
        _parent = parent;
    }

    public void Handle(HttpListenerContext context) {
        HttpListenerResponse response = context.Response;

        // Get query data
        string type = context.Request.QueryString["type"];
        string names = context.Request.QueryString["name"];
        string id = context.Request.QueryString["id"];
        string message = "";

        // Authenticate
        bool authorized = true;
        DeviceManager dm = _parent.GetDeviceManager();
        if (dm.FindDeviceByID(id) == null && dm.AuthenticationEnabled) {
            response.StatusCode = (int) HttpStatusCode.Unauthorized;
            message = "401 Unauthorized";
            authorized = false;
        }

        // Check what kind of data is requested
        if (type != null && authorized) {
            response.StatusCode = (int)HttpStatusCode.OK;
            switch (type) {
                case "cpu": message = HandleCPU(names); break;
                case "gpu": message = HandleGPU(names); break;
                case "fs": message = HandleFilesystems(); break;
                case "processes": message = HandleProcesses(context.Request.QueryString["sort"]); break;
                case "system": message = HandleSystem(names); break;
                case "disks": message = HandleDisks(); break;
                case "listdev": message = HandleListDevices(); break;
                case "history": message = HandleHistory(); break;
                case "historyfile": message = HandleHistoryFile(context.Request.QueryString["file"]); break;
                case "historylist": message = HandleHistoryList(context.Request.QueryString["sort"]); break;
                case "overview": message = HandleOverview(); break;
                case "addnotif": message = HandleAddNotification(context.Request.QueryString["id"], context.Request.QueryString["name"], 
                    context.Request.QueryString["ntype"], context.Request.QueryString["condition"], context.Request.QueryString["value"], context.Request.QueryString["ringonce"]);
                    break;
                case "listnotif": message = HandleGetNotifications(context.Request.QueryString["id"]); break;
                case "remnotif": message = HandleRemoveNotification(context.Request.QueryString["id"], context.Request.QueryString["index"]); break;
                case "testnotif": message = HandleTestNotification(context.Request.QueryString["id"]); break;
            }
        } else if (authorized) {
            response.StatusCode = (int) HttpStatusCode.BadRequest;
            message = "400 Bad request";
        }

        // Send the HTTP response to the client
        response.ContentType = "application/json";
        byte[] messageBytes = Encoding.Default.GetBytes(message);
        response.OutputStream.Write(messageBytes, 0, messageBytes.Length);     
        response.Close();
    }

    public string GetName() {
        return NAME;
    }
    
    private String HandleCPU(String names) {
        // Refresh
        _parent.UpdateComputer();

        bool namesb = false;
        if (names != null) { namesb = true; }
        // Generate JSON
        return JsonConvert.SerializeObject(new JSONCPUData(_parent.GetComputer(), namesb));
    }

    private String HandleGPU(String names) {
        // Refresh
        _parent.UpdateComputer();

        bool namesb = false;
        if (names != null) { namesb = true; }
        // Generate JSON
        return JsonConvert.SerializeObject(new JSONGPUData(_parent.GetComputer(), namesb));
    }

    private String HandleFilesystems() {
        // For each drive create a JSON object
        JSONFSData[] DataTemp = new JSONFSData[DriveInfo.GetDrives().Length];
        List<JSONFSData> JSON = new List<JSONFSData>();

        foreach (DriveInfo Drive in DriveInfo.GetDrives()) {
            if (Drive.IsReady) JSON.Add(new JSONFSData(Drive));
        }

        // Generate JSON
        return JsonConvert.SerializeObject(JSON);
    }
    
    private String HandleDisks() {
    	// Refresh
        _parent.UpdateComputer();

        // For each disk with a temp sensor create a JSON object
        List<JSONDiskData> json = new List<JSONDiskData>();

        foreach (IHardware h in _parent.GetComputer().Hardware.Where(h => h.HardwareType == HardwareType.HDD && h.Sensors.Length > 0)) {
            foreach (ISensor s in h.Sensors.Where(s => s.SensorType == SensorType.Temperature)) {
                json.Add(new JSONDiskData(h.Name, (float) s.Value));   
            }
        }
    	
    	// Generate JSON
    	return JsonConvert.SerializeObject(json);
    }

    private String HandleProcesses(String sort) {
        Process[] all = Process.GetProcesses();

        if (sort == "name") {
            return JsonConvert.SerializeObject(new JSONProcessData(all, JSONProcessDataSort.SORT_BY_NAME));
        } else {
            return JsonConvert.SerializeObject(new JSONProcessData(all, JSONProcessDataSort.SORT_BY_USAGE));
        }
    }

    private String HandleSystem(String names) {
        // Refresh
        _parent.UpdateComputer();
        
        // Generate JSON
        return JsonConvert.SerializeObject(new JSONSystemData(_parent.GetComputer(), names != null));
    }

    private String HandleListDevices() {
        // Refresh
        _parent.UpdateComputer();

        JSONDevicesList devList = new JSONDevicesList();

        IEnumerable<IHardware> matchingHardware = _parent.GetComputer().Hardware.Where(h => h.HardwareType == HardwareType.CPU || h.HardwareType == HardwareType.GpuAti
            || h.HardwareType == HardwareType.GpuNvidia || h.HardwareType == HardwareType.Mainboard);

        foreach (IHardware h in matchingHardware) {
            ISensor[] sensorList;
            HardwareType hardwareType;

            if (h.HardwareType == HardwareType.Mainboard && h.SubHardware.Length > 0) {
                sensorList = h.SubHardware[0].Sensors;
                hardwareType = h.SubHardware[0].HardwareType;
            } else {
                sensorList = h.Sensors;
                hardwareType = h.HardwareType;
            }

            IEnumerable<ISensor> matchingSensors = sensorList.Where(s => s.SensorType == SensorType.Clock || s.SensorType == SensorType.Fan || s.SensorType == SensorType.Load ||
                s.SensorType == SensorType.Power || s.SensorType == SensorType.Temperature || s.SensorType == SensorType.Voltage);

            foreach (ISensor s in matchingSensors) {
                JSONDeviceCategory cat;

                switch (hardwareType) {
                    case HardwareType.CPU: cat = JSONDeviceCategory.CPU; break;
                    case HardwareType.GpuAti: cat = JSONDeviceCategory.GPU; break;
                    case HardwareType.GpuNvidia: cat = JSONDeviceCategory.GPU; break;
                    case HardwareType.SuperIO: cat = JSONDeviceCategory.System; break;
                    default: cat = JSONDeviceCategory.Null; break;
                }

                devList.AddDevice(cat, s.Name, s.SensorType.ToString());
            }
        }

        return JsonConvert.SerializeObject(devList);
    }

    private String HandleHistory() {
        History history = _parent.GetHistory();

        if (history != null) return JsonConvert.SerializeObject(history.Data);
        else return "[]";
    }

    private String HandleOverview() {
        return JsonConvert.SerializeObject(new JSONOverview());
    }

    private String HandleHistoryList(String sort) {
        String[] paths = Directory.GetFiles(Path.Combine(Directory.GetCurrentDirectory(), "History"));
        String[,] files = new String[paths.Length, 2];

        if (sort != null && sort.ToLower() == "true") Array.Reverse(paths);        

        int i = 0;
        foreach (String path in paths) {
            History historyFile = JsonConvert.DeserializeObject<History>(File.ReadAllText(path));
            List<DatePoint> datePointList = historyFile.Data[0].Data;

            files[i, 0] = Path.GetFileName(path).Replace(".json", "");
            files[i, 1] = datePointList[datePointList.Count - 1].x;

            i++;
        }

        return JsonConvert.SerializeObject(files);
    }

    private String HandleHistoryFile(String file) {
        try {
            file = file.Replace(".", "").Replace(@"\", "").Replace("/", "");
            return File.ReadAllText(Path.Combine(Directory.GetCurrentDirectory(), "History", file + ".json"));
        } catch (Exception e) {
            return "[\"File not found!\"]";
        }
    }

    private String HandleAddNotification(String ID, String name, String type, String condititon, String value, String ringOnce) {
        DeviceManager dm = _parent.GetDeviceManager();

        Device device = dm.FindDeviceByID(ID);
        device.Notifications.Add(new Notification(name, type, condititon, Decimal.Parse(value), Boolean.Parse(ringOnce)));

        dm.SaveData();

        return "{}";
    }

    private String HandleGetNotifications(String ID) {
        Device device = _parent.GetDeviceManager().FindDeviceByID(ID);
        return JsonConvert.SerializeObject(device.Notifications);
    }

    private String HandleRemoveNotification(String ID, String index) {
        DeviceManager dm = _parent.GetDeviceManager();

        Device device = dm.FindDeviceByID(ID);
        int[] ia;

        try {
            ia = index.Split(';').Select(n => Convert.ToInt32(n)).ToArray();
        } catch (Exception e) {
            return "{}";
        }

        int removals = 0;
        foreach (int x in ia) {
            try { device.Notifications.RemoveAt(x - removals); removals++; } catch (ArgumentOutOfRangeException e) { }
        }

        dm.SaveData();

        return "{}";
    }

    private String HandleTestNotification(String id) {
        _parent.PushTestNotification(id);
        return "";
    }
}