using System;
using System.Windows.Forms;
using IcyMonitorServer.Properties;
using OpenHardwareMonitor.Hardware;
using IcyMonitorServer;
using Microsoft.Win32;
using PushSharp.Core;
using PushSharp.Android;
using PushSharp;
using System.Collections.Generic;
using System.IO;
using Newtonsoft.Json;


public class TrayApplicationContext : ApplicationContext {
    #region Private Members
    private System.ComponentModel.IContainer _components;   //List of components
    private NotifyIcon _notifyIcon;
    private ContextMenuStrip _contextMenu;
    private ToolStripMenuItem _showAbout;
    private ToolStripMenuItem _exitApp;
    private ToolStripMenuItem _openFirewall;
    private ToolStripMenuItem _runOnStartup;
    private ToolStripMenuItem _setupNotifications;
    private HttpServer _server;
    private Computer _computer;
    private UpdateVisitor _visitor;
    private List<Device> _push_devices = new List<Device>();
    private List<Notification> _push_data = new List<Notification>();
    private Settings _settings;
    private String _computerName;
    #endregion

    public TrayApplicationContext() {
        // Instantiate the component Module to hold everything
        _components = new System.ComponentModel.Container();

        // Load settings
        if (File.Exists("settings.dat")) { _settings = Serializer.DeSerializeObject<Settings>("settings.dat");} 
        else { _settings = new Settings(); } // Default settings if no file exists
        _computerName = _settings.ComputerName;

        // Create the icon
        _notifyIcon = new NotifyIcon(this._components);
        _notifyIcon.Icon = Resources.icon_app;
        _notifyIcon.Text = "Icy Monitor Server";
        _notifyIcon.Visible = true;

        // Create the context menu and it's items
        _contextMenu = new ContextMenuStrip();
        _showAbout = new ToolStripMenuItem();
        _exitApp = new ToolStripMenuItem();
        _openFirewall = new ToolStripMenuItem();
        _runOnStartup = new ToolStripMenuItem();
        _setupNotifications = new ToolStripMenuItem();

        //Attach the menu to the notify icon
        _notifyIcon.ContextMenuStrip = _contextMenu;

        //Setup the items and add them to the menu strip, adding handlers to be created later
        _showAbout.Text = "About";
        _showAbout.Click += new EventHandler(mDisplayForm_Click);
        _contextMenu.Items.Add(_showAbout);

        _openFirewall.Text = "Open Firewall";
        _openFirewall.Click += new EventHandler(mOpenFirewall_Click);
        _contextMenu.Items.Add(_openFirewall);

        _setupNotifications.Text = "Setup Notifications";
        _setupNotifications.Click += new EventHandler(mSetupNotifications_Click);
        _contextMenu.Items.Add(_setupNotifications);

        _runOnStartup.Text = "Run on startup";
        RegistryKey rk = Registry.CurrentUser.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);
        if (rk.GetValue("Icy Monitor Server") != null) { _runOnStartup.Checked = true; }
        _runOnStartup.CheckOnClick = true;
        _runOnStartup.Click += new EventHandler(mRunOnStartup_Click);
        _contextMenu.Items.Add(_runOnStartup);

        _exitApp.Text = "Exit";
        _exitApp.Click += new EventHandler(mExitApplication_Click);
        _contextMenu.Items.Add(_exitApp);

        // Create server
        _server = new HttpServer("http://*:28622/");

        // Create computer object from library
        _computer = new Computer();
        _visitor = new UpdateVisitor();

        _computer.CPUEnabled = true;
        _computer.FanControllerEnabled = true;
        _computer.GPUEnabled = true;
        _computer.MainboardEnabled = true;

        _computer.Open();
        _computer.Accept(_visitor);

        // Add the HttpRequestHandlers
        _server.AddHttpRequestHandler(new DataHttpRequestHandler(_computer, _visitor));
        _server.AddHttpRequestHandler(new AboutHttpRequestHandler());
        // Web interface. It's kinda broken right now.
        // mServer.AddHttpRequestHandler(new WebHttpRequestHandler());

        // Load notification data
        if (File.Exists("notif.dat")) {
            List<NotificationSerializable> serializable_notif;
            serializable_notif = Serializer.DeSerializeObject<List<NotificationSerializable>>("notif.dat");

            foreach (NotificationSerializable data in serializable_notif) {
                _push_data.Add(data.Expand(_computer));
            }
        }
        if (File.Exists("devices.dat")) {
            _push_devices = Serializer.DeSerializeObject<List<Device>>("devices.dat");
        }

        // Create timer
        Timer timer = new Timer();
        timer.Interval = _settings.RefreshRate;
        timer.Tick += timer_Tick;
        timer.Start();

        // Start the server
        if (_server.Start() == false) {
            base.ExitThreadCore();
        }
    }

    void timer_Tick(object sender, EventArgs e) {
        ((Timer)sender).Interval = _settings.RefreshRate;
        PushBroker push = new PushBroker();

        foreach (Notification notification in _push_data) {
            if (notification.HardwareAvailable) {
                ISensor sensor = notification.Sensor;
                switch (notification.Condition) {
                    case Condition.LessThan:
                        if (notification.Sent && sensor.Value >= (float)notification.Value) {
                            notification.Sent = false;
                        } else if (!notification.Sent && sensor.Value <= (float)notification.Value) {
                            notification.Sent = true;
                            SendNotificationToAll(push, notification);
                        }
                        break;
                    case Condition.MoreThan:
                        if (notification.Sent && sensor.Value <= (float)notification.Value) {
                            notification.Sent = false;
                        } else if (!notification.Sent && sensor.Value >= (float)notification.Value) {
                            notification.Sent = true;
                            SendNotificationToAll(push, notification);
                        }
                        break;
                }
            }
        }

        push.StopAllServices();
    }

    void SendNotificationToAll(PushBroker push, Notification notification) {
        string unit = GetUnit(notification.Sensor.SensorType);
        string avalue = notification.Value + unit;
        string cvalue = notification.Sensor.Value + unit;
        string title = _settings.ComputerName + " (" + notification.Sensor.Name + ")";
        string id = title + "_" + notification.Sensor.Name + "_" + unit;

        JSONNotification json = new JSONNotification(title, avalue, cvalue);

        foreach (Device device in _push_devices) {
            device.sendNotification(push, JsonConvert.SerializeObject(json));
        }
    }

    String GetUnit(SensorType type) {
        switch (type) {
            case SensorType.Clock: return "MHz";
            case SensorType.Fan: return "RPM";
            case SensorType.Load: return "%";
            case SensorType.Power: return "W";
            case SensorType.Temperature: return "°C";
            case SensorType.Voltage: return "V";
            default: return "";
        }
    }

    void mSetupNotifications_Click(object sender, EventArgs e) {
        _computer.Accept(_visitor);
        NotificationSetup win = new NotificationSetup(_computer, _push_data, _push_devices, _settings);
        win.ShowDialog();
    }

    void mDisplayForm_Click(object sender, EventArgs e) {
        About box = new About();
        box.ShowDialog();
    }

    void mExitApplication_Click(object sender, EventArgs e) {
        //Call our overridden exit thread core method!
        ExitThreadCore();
    }

    protected override void ExitThreadCore() {
        _server.Stop();
        _computer.Close();

        //Call the base method to exit the application
        base.ExitThreadCore();
    }

    void mOpenFirewall_Click(object sender, EventArgs e) {
        System.Diagnostics.Process.Start("netsh", " advfirewall firewall add rule name=\"Icy Monitor Server\" dir=in action=allow protocol=TCP localport=28622");
    }

    private void mRunOnStartup_Click(object sender, EventArgs e) {
        RegistryKey rk = Registry.LocalMachine.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);

        if (_runOnStartup.Checked) { rk.SetValue("Icy Monitor Server", Application.ExecutablePath.ToString()); } 
        else { rk.DeleteValue("Icy Monitor Server", false); }
    }
}