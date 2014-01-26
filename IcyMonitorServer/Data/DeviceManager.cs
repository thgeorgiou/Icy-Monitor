using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Windows.Forms;

public class DeviceManager {
    private List<Device> _devices;
    public bool AuthenticationEnabled { get; set; }

    public DeviceManager(Settings settings) {
        AuthenticationEnabled = settings.AuthorizedDevices;
        LoadData();
    }

    // Load data from disk
    public void LoadData() {
        if (File.Exists("devices.json")) {
            try {
                //_devices = Serializer.DeSerializeObject<List<Device>>("devices.dat");
                _devices = JsonConvert.DeserializeObject<List<Device>>(File.ReadAllText("devices.json"));
            } catch (Exception e) {
                MessageBox.Show("Could not load saved devices.");
                _devices = new List<Device>();
                File.WriteAllText("devices.json", JsonConvert.SerializeObject(_devices));
            }
        } else {
            _devices = new List<Device>();
        }
    }

    // Save data from disk
    public void SaveData() {
        //Serializer.SerializeObject<List<Device>>(_devices, "devices.dat");
        File.WriteAllText("devices.json", JsonConvert.SerializeObject(_devices));
    }

    // Return device with given name.
    public Device FindDeviceByName(String name) {
        foreach (Device device in _devices) {
            if (device.Name.Equals(name)) return device;
        }

        // In case nothing is found return null
        return null;
    }

    // Return device with given unique ID.
    public Device FindDeviceByID(String id) {
        foreach (Device device in _devices) {
            if (device.ID.Equals(id)) return device;
        }

        // In case nothing is found return null
        return null;
    }

    // Add a new device to this list
    public void AddDevice(Device device) {
        if(FindDeviceByID(device.ID) == null) _devices.Add(device);
    }

    // Remove a device
    public void RemoveDevice(Device device) {
        _devices.Remove(device);
    }

    // Remove a device by name
    public void RemoveDeviceByName(String name) {
        RemoveDevice(FindDeviceByName(name));
    }

    // Remove a device by ID
    public void RemoveDeviceByID(String id) {
        RemoveDevice(FindDeviceByID(id));
    }

    // Returns a device.
    public Device Get(int index) {
        return _devices[index];
    }

    public int GetCount() {
        return _devices.Count;
    }
}
