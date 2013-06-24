using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

class JSONDiskData {
    public string Name, Label, Format;
    public double Size, Free;

    public JSONDiskData(DriveInfo Drive) {
        Name = Drive.Name;
        Label = Drive.VolumeLabel;
        Format = Drive.DriveFormat;

        // Bytes to MB
        Size = Drive.TotalSize / (1024 * 1024);
        Free = Drive.TotalFreeSpace / (1024 * 1024);

        // Round
        Size = Math.Round(Size, 2, MidpointRounding.ToEven);
        Free = Math.Round(Free, 2, MidpointRounding.ToEven);

        // MB to GB and round
        Size = Math.Round(Size / 1024, 1, MidpointRounding.ToEven);
        Free = Math.Round(Free / 1024, 1, MidpointRounding.ToEven);
    }
}