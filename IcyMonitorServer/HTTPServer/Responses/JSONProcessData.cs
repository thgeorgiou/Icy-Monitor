using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Runtime.InteropServices;

class JSONProcessData {
    public JSONSingleProcessData[] Processes;
    public uint TotalMem;
    public uint UsedMem;

    public JSONProcessData(Process[] proc, int sort) {
        Int64 phav = PerformanceInfo.GetPhysicalAvailableMemoryInMiB();
        Int64 tot = PerformanceInfo.GetTotalMemoryInMiB();
        UsedMem = (uint) Math.Round(100 - (((decimal) phav / (decimal) tot) * 100));
        TotalMem = (uint) tot;

        Processes = new JSONSingleProcessData[proc.Length];
        int i = 0;
        
        foreach (Process process in proc) {
            Processes[i] = new JSONSingleProcessData(process);
            i++;
        }

        switch (sort) {
            case JSONProcessDataSort.SORT_BY_NAME:
                Array.Sort(Processes, delegate(JSONSingleProcessData proc1, JSONSingleProcessData proc2) {
                    return proc1.Name.CompareTo(proc2.Name);
                });
                break;
            case JSONProcessDataSort.SORT_BY_USAGE:
                Array.Sort(Processes, delegate(JSONSingleProcessData proc1, JSONSingleProcessData proc2) {
                    return proc1.Mem.CompareTo(proc2.Mem);
                });
                Array.Reverse(Processes);
                break;
        }
    }
}

class JSONSingleProcessData {
    public String Name;
    public double Mem;

    public JSONSingleProcessData(Process proc) {
        Name = proc.ProcessName;
        Mem = proc.WorkingSet64 / 1024;
        Mem = Mem / 1024;

        // Round
        Mem = Math.Round(Mem, 1, MidpointRounding.ToEven);
    }
}

static class JSONProcessDataSort {
    public const int SORT_BY_NAME = 0;
    public const int SORT_BY_USAGE = 1;
}


public static class PerformanceInfo {
    [DllImport("psapi.dll", SetLastError = true)]
    [return: MarshalAs(UnmanagedType.Bool)]
    public static extern bool GetPerformanceInfo([Out] out PerformanceInformation PerformanceInformation, [In] int Size);

    [StructLayout(LayoutKind.Sequential)]
    public struct PerformanceInformation {
        public int Size;
        public IntPtr CommitTotal;
        public IntPtr CommitLimit;
        public IntPtr CommitPeak;
        public IntPtr PhysicalTotal;
        public IntPtr PhysicalAvailable;
        public IntPtr SystemCache;
        public IntPtr KernelTotal;
        public IntPtr KernelPaged;
        public IntPtr KernelNonPaged;
        public IntPtr PageSize;
        public int HandlesCount;
        public int ProcessCount;
        public int ThreadCount;
    }

    public static Int64 GetPhysicalAvailableMemoryInMiB() {
        PerformanceInformation pi = new PerformanceInformation();
        if (GetPerformanceInfo(out pi, Marshal.SizeOf(pi))) {
            return Convert.ToInt64((pi.PhysicalAvailable.ToInt64() * pi.PageSize.ToInt64() / 1048576));
        } else {
            return -1;
        }

    }

    public static Int64 GetTotalMemoryInMiB() {
        PerformanceInformation pi = new PerformanceInformation();
        if (GetPerformanceInfo(out pi, Marshal.SizeOf(pi))) {
            return Convert.ToInt64((pi.PhysicalTotal.ToInt64() * pi.PageSize.ToInt64() / 1048576));
        } else {
            return -1;
        }

    }
}
