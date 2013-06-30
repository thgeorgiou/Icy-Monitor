using System;

interface JSONNameValue { }

class JSONNameValueInt : JSONNameValue{
    public String Name;
    public int Value;

    public JSONNameValueInt(String name, int value) {
        this.Name = name;
        this.Value = value;
    }
}

class JSONNameValueFloat : JSONNameValue{
    public String Name;
    public float Value;

    public JSONNameValueFloat(String name, float value) {
        this.Name = name;
        this.Value = value;
    }

    public JSONNameValueFloat(String name, float value, int round) {
        this.Name = name;
        this.Value = (float) Math.Round(value, round, MidpointRounding.ToEven);
    }
}