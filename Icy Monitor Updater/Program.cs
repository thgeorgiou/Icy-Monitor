using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Icy_Monitor_Updater {
    class Program {
        static void Main(string[] args) {
            Console.WriteLine("Icy Monitor Updater v1.0");
            Console.WriteLine("By Thanasis Georgiou");
            Console.WriteLine("\nLooking for updates...");

            if (!Directory.Exists(@"update/")) {
                Console.WriteLine("No updates found. Exiting...");
                Environment.Exit(0);
            }

            Console.WriteLine("Updates found. Copying files...");

            foreach (String file in Directory.GetFiles(@"update/")) {
                if (file != "update.exe") {
                    String fName = file.Substring(@"update/".Length);
                    Console.WriteLine("Copying " + file + " to " + fName);
                    try {
                        File.Copy(file, fName, true);
                    } catch (Exception e) {
                        Console.WriteLine("Could not copy. Reason: " + e.Message);
                        Console.WriteLine("Press any key to exit.");
                        Console.Read();
                        Environment.Exit(1);
                    }
                }
            }

            Console.WriteLine("Cleaning up...");
            foreach (String file in Directory.GetFiles(@"update/")) {
                try { File.Delete(file); } catch (Exception e) {}
            }

            try { Directory.Delete(@"update/"); } catch (Exception e) {}

            try { File.Delete(@"update.zip"); } catch (Exception e) { }

            Console.WriteLine("Finished! Starting Icy Monitor...");
            try {
                Process.Start(@"IcyMonitorServer.exe");
            } catch (Exception e) {
                Console.WriteLine("Could not run IcyMonitor. Reason: " + e.Message);
                Console.WriteLine("Press any key to exit.");
                Console.Read();
                Environment.Exit(1);
            }
            Environment.Exit(0);
        }
    }
}
