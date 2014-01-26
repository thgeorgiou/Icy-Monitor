using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

public class Notification {
    public String Name, Type, Condition;
    public Decimal Value;
    public Boolean RingOnce;
    private Boolean _hasRang;

    public Notification(String name, String type, String condition, Decimal value, Boolean ringOnce) {
        this.Name = name;
        this.Type = type;
        this.Condition = condition; // >= and =<
        this.Value = value;
        this.RingOnce = ringOnce;
    }

    public Boolean HasRang() {
        return _hasRang;
    }

    public void ToggleHasRang() {
        if (_hasRang) _hasRang = false;
        else _hasRang = true;
    }
}