using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Windows.Forms;

public class Device {
    public String Name { get; set; }
    public String ID { get; set; }
    public String GCM { get; set; }
    public Boolean Allowed { get; set; }
    public List<Notification> Notifications;

    public Device(String name, String id, String gcm, Boolean allowed) {
        Name = name;
        ID = id;
        GCM = gcm;
        Allowed = allowed;
        Notifications = new List<Notification>();
    }
}
