using Newtonsoft.Json;
using PushSharp;
using PushSharp.Android;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

public class NotificationManager {
    PushBroker _push;
    String _compName;

    public NotificationManager(String compName) {
        _push = new PushBroker();
        _push.RegisterGcmService(new GcmPushChannelSettings("AIzaSyAutUa56XnPXuJaPtQk7dATEpvkzu7p1aE"));

        _compName = compName;
    }

    public bool PushNotification(Device device, JSONNotification notification) {
        // Check if gcm looks is valid
        if (!String.IsNullOrEmpty(device.GCM) && !String.IsNullOrWhiteSpace(device.GCM)) {
            try {
                GcmNotification notif = new GcmNotification().ForDeviceRegistrationId(device.GCM).WithJson(JsonConvert.SerializeObject(notification));
                _push.QueueNotification(notif);
                return true;
            } catch (Exception e) {
                return false;
            }            
        } else {
            return false;
        }        
    }

    public bool PushTestNotification(Device device) {
        JSONNotification notification = new JSONNotification("IcyTestNotification", _compName, "");
        return PushNotification(device, notification);
    }

    public void StopService() {
        _push.StopAllServices();
    }
}

public class JSONNotification {
    public String SensorName, CompName, Value;

    public JSONNotification(String name, String compName, String value) {
        this.SensorName = name;
        this.CompName = compName;
        this.Value = value;
    }
}