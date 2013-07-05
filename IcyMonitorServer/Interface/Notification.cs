using OpenHardwareMonitor.Hardware;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;

public enum Condition { MoreThan, LessThan }

public class Notification {
    #region Private
    private IHardware _hardware;
    private ISensor _sensor;
    private Condition _condition;
    private decimal _value;
    private Boolean _hardwareAvailable;
    #endregion

    #region Public Getters/Setters
    public string HardwareName {
        get {
            if (_hardwareAvailable) { return _hardware.Name; } else { return "Unavailable"; }
        }
    }
    public string SensorName {
        get {
            if (_hardwareAvailable) { return _sensor.Name; } else { return "Unavailable"; }
        }
    }
    public IHardware Hardware {
        get { return _hardware; }
    }
    public ISensor Sensor {
        get { return _sensor; }
    }
    public Condition Condition {
        get { return _condition; }
    }
    public decimal Value {
        get { return _value; }
    }
    public Boolean Sent { get; set; }
    public Boolean HardwareAvailable {
        get { return _hardwareAvailable; }
    }
    #endregion

    public Notification(Computer computer, String HardwareName, String SensorName, Condition condition, decimal value, SensorType sensorType) {
        // Find hardware and store it
        foreach (IHardware hardware in computer.Hardware) {
            if (hardware.Name == HardwareName) {
                _hardware = hardware;
                foreach (ISensor sensor in hardware.Sensors) {
                    if (sensor.Name == SensorName && sensor.SensorType == sensorType) { _sensor = sensor; }
                }
            } else if (hardware.HardwareType == HardwareType.Mainboard) {
                if (hardware.SubHardware[0].Name == HardwareName) {
                    _hardware = hardware.SubHardware[0];
                    foreach (ISensor sensor in hardware.SubHardware[0].Sensors) {
                        if (sensor.Name == SensorName && sensor.SensorType == sensorType) { _sensor = sensor; }
                    }
                }
            }
        }
        
        // Store condition and value
        _condition = condition;
        _value = value;

        // Check if hardware is not available
        _hardwareAvailable = !(_sensor == null || _hardware == null);
    }

    public Notification(IHardware hardware, ISensor sensor, Condition condition, decimal value) {
        _hardware = hardware;
        _sensor = sensor;
        _condition = condition;
        _value = value;
        _hardwareAvailable = true;
    }

    public override string ToString() {
        String condition = "";
        switch (_condition) {
            case Condition.LessThan: condition = " =< "; break;
            case Condition.MoreThan: condition = " >= "; break;
        }
        return SensorName + " of " + HardwareName + condition + Value;
    }

    public NotificationSerializable GetSerializable() {
        return new NotificationSerializable(this);
    }
}

[Serializable()] 
public class NotificationSerializable : ISerializable{
    private string _hardwareName;
    private string _sensorName;
    private SensorType _sensorType;
    private decimal _value;
    private Condition _condition;

    public NotificationSerializable(Notification data) {
        _hardwareName = data.HardwareName;
        _sensorName = data.SensorName;
        _condition = data.Condition;
        _value = data.Value;
        _sensorType = data.Sensor.SensorType;
    }

    // Deserialization constructor.
    public NotificationSerializable(SerializationInfo info, StreamingContext ctxt) {
        _hardwareName = (string) info.GetValue("hardwareName", typeof(string));
        _sensorName = (string) info.GetValue("sensorName", typeof(string));
        _condition = (Condition) info.GetValue("condition", typeof(Condition));
        _value = (decimal) info.GetValue("value", typeof(decimal));
        _sensorType = (SensorType) info.GetValue("sensorType", typeof(SensorType));
    }
        
    // Serialization function.
    public void GetObjectData(SerializationInfo info, StreamingContext ctxt) {
        info.AddValue("hardwareName", _hardwareName);
        info.AddValue("sensorName", _sensorName);
        info.AddValue("condition", _condition);
        info.AddValue("value", _value);
        info.AddValue("sensorType", _sensorType);
    }

    public Notification Expand(Computer computer) {
        return new Notification(computer, _hardwareName, _sensorName, _condition, _value, _sensorType);
    }
}