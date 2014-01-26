using Newtonsoft.Json;
using System;
using System.IO;
using System.Windows.Forms;

public class Settings{
    public String ComputerName { get; set; }
    public int Port { get; set; }
    public bool AuthorizedDevices { get; set; }
    public bool Multicast { get; set; }
    public bool History { get; set; }
    public bool KeepHistory { get; set; } 

    public Settings() {
        SetDefault();
    }

    public void SetDefault() {
        ComputerName = System.Environment.MachineName;
        Port = 28622;
        AuthorizedDevices = true;
        Multicast = true;
        History = true;
        KeepHistory = true;
    }

    public void Save() {
        try {
            File.WriteAllText("settings.json", JsonConvert.SerializeObject(this));
        } catch (IOException e) {
            MessageBox.Show("Could not save settings.\n" + e.Message);
        }
    }
}