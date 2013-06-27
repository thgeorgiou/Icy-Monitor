using System;

interface JSONNameValue { }

class JSONNameValueInt : JSONNameValue{
    public String Name;
    public int Value, Min, Max;

    public JSONNameValueInt(String name, int value, int min, int max) {
        this.Name = name;
        this.Value = value;
        this.Min = min;
        this.Max = max;
    }
}

class JSONNameValueFloat : JSONNameValue{
    public String Name;
    public float Value, Min, Max;

    public JSONNameValueFloat(String name, float value, float min, float max) {
        this.Name = name;
        this.Value = value;
        this.Min = min;
        this.Max = max;
    }

    public JSONNameValueFloat(String name, float value, float min, float max, int round) {
        this.Name = name;
        this.Value = (float) Math.Round(value, round, MidpointRounding.ToEven);
    }
}