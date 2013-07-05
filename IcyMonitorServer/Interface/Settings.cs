using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;

[Serializable()] 
public class Settings : ISerializable {
    public int RefreshRate { get; set; }
    public String ComputerName { get; set; }

    // Default settings
    public Settings() {
        RefreshRate = 60000;
        ComputerName = System.Environment.MachineName;
    }

    // Deserialization constructor.
    public Settings(SerializationInfo info, StreamingContext ctxt) {
        RefreshRate = (int) info.GetValue("refreshRate", typeof(int));
        ComputerName = (string) info.GetValue("computerName", typeof(string));
    }

    // Serialization function.
    public void GetObjectData(SerializationInfo info, StreamingContext ctxt) {
        info.AddValue("refreshRate", RefreshRate);
        info.AddValue("computerName", ComputerName);
    }
}