using PushSharp;
using PushSharp.Android;
using System;
using System.Runtime.Serialization;
using System.Text;


[Serializable()] 
public class Device : ISerializable{
    private String _regId;
    private String _name;

    public String Name {
        get { return _name; }
    }
    public String RegistrationID {
        get { return _regId; }
    }

    public Device(String name, String regId) {
        _name = name;
        _regId = regId;
    }

    // Deserialization constructor.
    public Device(SerializationInfo info, StreamingContext ctxt) {
        _name = (string) info.GetValue("name", typeof(string));
        _regId = (string) info.GetValue("regId", typeof(string));
    }
        
    // Serialization function.
    public void GetObjectData(SerializationInfo info, StreamingContext ctxt) {
        info.AddValue("name", _name);
        info.AddValue("regId", _regId);
    }

    public void sendNotification(PushBroker push, String data) {
        //Registering the GCM Service and sending an Android Notification
        push.RegisterGcmService(new GcmPushChannelSettings("AIzaSyAutUa56XnPXuJaPtQk7dATEpvkzu7p1aE"));
        push.QueueNotification(new GcmNotification().ForDeviceRegistrationId(_regId).WithJson(data));
    }

    public override string ToString() {
        return _name;
    }
}
