using System;
using OpenHardwareMonitor.Hardware;
using System.Windows.Forms;

public class Program {
    [STAThread]
    public static void Main(string[] args) {
        // Create the tray icon
        Application.EnableVisualStyles();
        Application.SetCompatibleTextRenderingDefault(false);
        Application.Run(new TrayApplicationContext());
    }
}