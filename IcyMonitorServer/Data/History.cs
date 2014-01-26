using Newtonsoft.Json;
using OpenHardwareMonitor.Hardware;
using System;
using System.Collections.Generic;
using System.IO;

public class History {
    public List<Line> Data { get; set; }
    private List<ISensor> _sensors;
    private String _filename;
    public Boolean KeepHistory = false;

    public History(Boolean save) {
        Data = new List<Line>();
        _sensors = new List<ISensor>();

        if (save) {
            KeepHistory = true;

            DateTime now = DateTime.Now;
            _filename = now.ToString("yyyy-MM-dd_HH-mm") + ".json";

            if (!Directory.Exists(Path.Combine(Directory.GetCurrentDirectory(), @"\History"))) {
                Directory.CreateDirectory(Path.Combine(Directory.GetCurrentDirectory(), @"\History"));
            }
        }
    }

    public int AddSensor(ISensor sensor) {
        _sensors.Add(sensor);
        Data.Add(new Line(sensor.Name));
        return _sensors.Count;
    }

    public void Clear() {
        Data.Clear();
        _sensors.Clear();
    }

    public void Tick(Object o, EventArgs e) {
        for (int i = 0; i < _sensors.Count; i++) {
            ISensor sensor = _sensors[i];
            Line line = Data[i];

            line.Add(new DatePoint(GetNowString(), (float) sensor.Value));
        }

        if (KeepHistory) {
            String output = JsonConvert.SerializeObject(this);
            File.WriteAllText(Path.Combine(Directory.GetCurrentDirectory(), @"History", _filename), output);
        }
    }

    public String GetNowString() {
        DateTime now = DateTime.Now;

        String hour, minute;
        if (now.Hour > 9) hour = now.Hour.ToString();
        else hour = "0" + now.Hour;

        if (now.Minute > 9) minute = now.Minute.ToString();
        else minute = "0" + now.Minute;

        return now.Year + "/" + now.Month + "/" + now.Day + " " + hour + ":" + minute;
    }
}

public class Line {
    public readonly String Name;
    public readonly List<DatePoint> Data;

    public Line(String name) {
        Name = name;
        Data = new List<DatePoint>();
    }

    public void Add(DatePoint data) {
        Data.Add(data);
    }
}

public class DatePoint {
    public readonly String x;
    public readonly float y;

    public DatePoint(String x, float y) {
        this.x = x;
        this.y = y;
    }
}