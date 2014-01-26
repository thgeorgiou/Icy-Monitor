using System;
using System.Runtime.InteropServices;
using System.Management;


class JSONOverview {
    [DllImport("kernel32")]
    extern static UInt64 GetTickCount64();

    private UInt64 Uptime {
        get {
            try {
                return GetTickCount64();
            } catch (EntryPointNotFoundException e) { // For windows server 2003
                return 0;
            }
        }
    }
	
    private static string GetOSFriendlyName() {
        string result = string.Empty;
        ManagementObjectSearcher searcher = new ManagementObjectSearcher("SELECT Caption FROM Win32_OperatingSystem");
        foreach (ManagementObject os in searcher.Get()) {
            result = os["Caption"].ToString();
            break;
        }
        return result;
    }

    public readonly int d, h, m;
    public readonly String LastBoot;
    public readonly String OSName, OSArch;
    public readonly String Hostname;

    public JSONOverview() {
        // Uptime
        TimeSpan t = TimeSpan.FromMilliseconds(Uptime);
        d = t.Days;
        h = t.Hours;
        m = t.Minutes;

        // Last boot
        DateTime lastBoot = DateTime.Now.Subtract(t);
        LastBoot = lastBoot.Year + "/" + lastBoot.Month + "/" + lastBoot.Day + " " + lastBoot.Hour + ":" + lastBoot.Minute;

        // OS
        OSName = GetOSFriendlyName();
        if (Environment.Is64BitOperatingSystem) OSArch = "64";
        else OSArch = "32";

        // Hostname
        Hostname = System.Environment.MachineName;
    }
}