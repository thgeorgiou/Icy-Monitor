using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;


class JSONNotification {
    public string Title, AValue, CValue;
    public int ID;

    public JSONNotification(string title, string avalue, string cvalue) {
        Title = title;
        ID = new Random().Next(0, 100);
        AValue = avalue;
        CValue = cvalue;
    }
}