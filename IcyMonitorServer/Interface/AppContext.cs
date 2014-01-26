using IcyMonitorServer.Interface;
using IcyMonitorServer.Properties;
using Microsoft.Win32;
using Newtonsoft.Json;
using OpenHardwareMonitor.Hardware;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Windows.Forms;
using System.Linq;


public class TrayApplicationContext : ApplicationContext {
    // Context
    private System.ComponentModel.IContainer _components;   //List of components
    private NotifyIcon _notifyIcon;

    // Context Menu
    private ContextMenuStrip _contextMenu;
    private ToolStripMenuItem _showAbout;
    private ToolStripMenuItem _exitApp;
    private ToolStripMenuItem _openFirewall;
    private ToolStripMenuItem _runOnStartup;
    private ToolStripMenuItem _settingsItem;
    private ToolStripMenuItem _checkForUpdatesItem;

    // Server and computer
    private HttpServer _server;
    private Computer _computer;
    private UpdateVisitor _visitor;

    // App settings
    private Settings _settings;
    private DeviceManager _deviceManager;

    // UDP multicasting (for autodetection)
    private Socket _multicastSocket;
    private byte[] _multicastMessage;
    private Timer _multicastTimer;

    // History
    private History _history;
    private Timer _historyTimer;

    // Notification Manager
    private NotificationManager _notificationManager;
    private List<ISensor> _sensors;

    public TrayApplicationContext() {
        _components = new System.ComponentModel.Container();

        // Clean old files if they exist
        if (File.Exists("settings.dat")) {
            File.Delete("settings.dat");
            MessageBox.Show("All stored preferences were cleared during the update.\nPlease set your preferences again by right-clicking the tray icon and selecting settings.");
        }
        if (File.Exists("devices.dat")) File.Delete("devices.dat");

        // Load settings
        Boolean first_time = false;
        if (File.Exists("settings.json")) {
            try {
                _settings = JsonConvert.DeserializeObject<Settings>(File.ReadAllText("settings.json"));
            } catch (Exception e) {
                MessageBox.Show("Could not load settings. Default values will be used.");

                _settings = new Settings();
                File.WriteAllText("settings.json", JsonConvert.SerializeObject(_settings));
            }
        } else {
            _settings = new Settings();
            _settings.Save();
            first_time = true;
        }

        // Create history directory if needed
        if (!Directory.Exists("History")) {
            Directory.CreateDirectory("History");
        }

        // Load devices
        _deviceManager = new DeviceManager(_settings);
        _deviceManager.SaveData();

        // Prepare notification system
        _notificationManager = new NotificationManager(_settings.ComputerName);

        // Create the icon
        _notifyIcon = new NotifyIcon(this._components);
        _notifyIcon.Icon = Resources.icon_app;
        _notifyIcon.Text = "Icy Monitor Server";
        _notifyIcon.DoubleClick += mSettings_Click;
        _notifyIcon.Visible = true;

        // Prepare context menu
        PrepareContextMenu();

        // Create computer object for data
        _computer = new Computer();
        _visitor = new UpdateVisitor();

        _computer.CPUEnabled = true;
        _computer.FanControllerEnabled = true;
        _computer.GPUEnabled = true;
        _computer.MainboardEnabled = true;
        _computer.HDDEnabled = true;

        _computer.Open();
        _computer.Accept(_visitor);

        // Collect sensors (for history and notifications)
        CollectSensors();

        // Create notifications timer
        StartNotificationsTimer();

        // Create history timer
        if (_settings.History) StartHistoryTimer();

        // Create server
        CreateServer();

        // Create the multicast timer
        if (_settings.Multicast) {
            try {
                StartMulticasting();
            } catch (SocketException e) {
                MessageBox.Show("Could not open socket for multicasting. Autodetection will be disabled.\n(Is another copy of Icy Monitor running?)");
            }            
        }

        if (first_time) {
            DialogResult dialogResult = MessageBox.Show("It is essential that you open a port for the server to use, would you want to open the preferences window?\n\nYou can do this at any moment by double-clicking the tray icon.",
                "Icy Monitor Server", MessageBoxButtons.YesNo);
            if (dialogResult == DialogResult.Yes) {
                mSettings_Click(null, null);
            } 
        }

        // Start the server
        if (_server.Start() == false) {
            if (_multicastSocket != null) _multicastSocket.Close();
            base.ExitThreadCore();
        }
    }

    void PrepareContextMenu() {
        // Create the context menu and it's items
        _contextMenu = new ContextMenuStrip();
        _showAbout = new ToolStripMenuItem();
        _exitApp = new ToolStripMenuItem();
        _openFirewall = new ToolStripMenuItem();
        _runOnStartup = new ToolStripMenuItem();
        _settingsItem = new ToolStripMenuItem();
        _checkForUpdatesItem = new ToolStripMenuItem();

        // Attach the menu to the notify icon
        _notifyIcon.ContextMenuStrip = _contextMenu;

        // Setup the items and add them to the menu strip, adding handlers to be created later
        _showAbout.Text = "About";
        _showAbout.Click += new EventHandler(mDisplayForm_Click);
        _contextMenu.Items.Add(_showAbout);

        _settingsItem.Text = "Settings";
        _settingsItem.Click += new EventHandler(mSettings_Click);
        _contextMenu.Items.Add(_settingsItem);

        _checkForUpdatesItem.Text = "Check for updates";
        _checkForUpdatesItem.Click += new EventHandler(mCheckForUpdates_Click);
        _contextMenu.Items.Add(_checkForUpdatesItem);

        _exitApp.Text = "Exit";
        _exitApp.Click += new EventHandler(mExitApplication_Click);
        _contextMenu.Items.Add(_exitApp);
    }

    void CreateServer() {
        _server = new HttpServer("http://*:" + _settings.Port + "/");

        // Add the HttpRequestHandlers
        _server.AddHttpRequestHandler(new DataHttpRequestHandler(this));
        _server.AddHttpRequestHandler(new AuthHttpRequestHandler(_deviceManager));
        _server.AddHttpRequestHandler(new AuthEnabledHttpRequesthandler(_deviceManager));
        _server.AddHttpRequestHandler(new RegisterHttpRequestHandler(_deviceManager));
        _server.AddHttpRequestHandler(new InvalidHttpRequestHandler());
    }

    void StartMulticasting() {
        IPHostEntry host;
        IPAddress localIP = null;
        host = Dns.GetHostEntry(Dns.GetHostName());
        foreach (IPAddress lip in host.AddressList) {
            if (lip.AddressFamily.ToString() == "InterNetwork") {
                localIP = lip;
            }
        }

        IPAddress ip = IPAddress.Parse("224.6.7.8");

        _multicastSocket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
        _multicastSocket.Bind(new IPEndPoint(localIP, 9584));

        _multicastSocket.SetSocketOption(SocketOptionLevel.IP, SocketOptionName.AddMembership, new MulticastOption(ip, localIP));
        _multicastSocket.SetSocketOption(SocketOptionLevel.IP, SocketOptionName.MulticastTimeToLive, 1);

        IPEndPoint ipep = new IPEndPoint(ip, 9584);
        _multicastSocket.Connect(ipep);

        _multicastMessage = System.Text.Encoding.ASCII.GetBytes("IcyMonitor-" + _settings.ComputerName);

        _multicastTimer = new Timer();
        _multicastTimer.Interval = 15000;
        _multicastTimer.Tick += multicastTimer_Tick;
        _multicastTimer.Start();
    }

    void multicastTimer_Tick(object sender, EventArgs e) {
        try {
            _multicastSocket.Send(_multicastMessage, _multicastMessage.Length, SocketFlags.None);
        } catch (SocketException ex) {
            MessageBox.Show("Problem with multicasting. Are two copies of Icy Monitor running at once?");
            Application.Exit();
        }
    }

    void StartHistoryTimer() {
        // Create history and add sensors
        _history = new History(_settings.KeepHistory);

        _computer.Accept(_visitor);

        IEnumerable<ISensor> matchingSensors = _sensors.Where(s => s.SensorType == SensorType.Temperature);
        foreach (ISensor sensor in matchingSensors) {
            _history.AddSensor(sensor);
        }
        
        _historyTimer = new Timer();
        _historyTimer.Interval = 5 * 30 * 1000; // Every 2.5 minutes.
        _historyTimer.Tick += _history.Tick;

        _history.Tick(null, null); // First batch of data 
        _history.Tick(null, null);

        _historyTimer.Start();
    }

    private void CollectSensors() {
        _sensors = new List<ISensor>();

        IEnumerable<IHardware> matchingHardware = _computer.Hardware.Where(h => h.HardwareType == HardwareType.CPU || h.HardwareType == HardwareType.GpuAti
            || h.HardwareType == HardwareType.GpuNvidia || h.HardwareType == HardwareType.Mainboard);

        foreach (IHardware h in matchingHardware) {
            if (h.HardwareType == HardwareType.Mainboard && h.SubHardware.Length > 0) {
                _sensors.AddRange(h.SubHardware[0].Sensors);
            } else {
                _sensors.AddRange(h.Sensors);
            }
        }
    }

    private void StartNotificationsTimer() {
        // Create a 5 second timer for notifications
        Timer timer = new Timer();
        timer.Interval = 5000;
        timer.Tick += NotificationTimer_Tick;
        timer.Start();
    }

    void NotificationTimer_Tick(object sender, EventArgs e) {
        int count = _deviceManager.GetCount();
        _computer.Accept(_visitor);

        for (int i = 0; i < count; i++) {
            foreach (Notification notif in _deviceManager.Get(i).Notifications) {
                ISensor sensor = _sensors.FirstOrDefault(s => s.Name == notif.Name && s.SensorType.ToString() == notif.Type);

                if (sensor != null) {
                    if (notif.RingOnce) {
                        if (!notif.HasRang()) {
                            if (IsConditionSatisfied((decimal)sensor.Value, notif.Value, notif.Condition)) {
                                _notificationManager.PushNotification(_deviceManager.Get(i), new JSONNotification(sensor.Name, _settings.ComputerName, sensor.Value + GetUnit(sensor)));
                                notif.ToggleHasRang();
                            }
                        } else if (!IsConditionSatisfied((decimal)sensor.Value, notif.Value, notif.Condition)) {
                            notif.ToggleHasRang();
                        }
                    } else if (IsConditionSatisfied((decimal)sensor.Value, notif.Value, notif.Condition)) {
                        _notificationManager.PushNotification(_deviceManager.Get(i), new JSONNotification(sensor.Name, _settings.ComputerName, sensor.Value + GetUnit(sensor)));
                    }
                }
            }
        }
    }

    bool IsConditionSatisfied(Decimal SensorValue, Decimal NotifValue, String Condition) {
        if (Condition == ">=") return SensorValue >= NotifValue;
        else return SensorValue <= NotifValue;
    }

    String GetUnit(ISensor sensor) {
        switch (sensor.SensorType) {
            case SensorType.Clock: return "mHz";
            case SensorType.Fan: return "RPM";
            case SensorType.Load: return "%";
            case SensorType.Power: return "W";
            case SensorType.Temperature: return " °C";
            case SensorType.Voltage: return "V";
            default: return "";
        }
    }

    void mDisplayForm_Click(object sender, EventArgs e) {
        About box = new About();
        box.ShowDialog();
    }

    void mSettings_Click(object sender, EventArgs e) {
        SettingsDialog dialog = new SettingsDialog(_deviceManager, _settings);
        DialogResult result = dialog.ShowDialog();

        if (result == DialogResult.OK) {
            _settings.Save();

            // Apply settings
            _deviceManager.AuthenticationEnabled = _settings.AuthorizedDevices;

            if (_multicastSocket != null && !_settings.Multicast) {
                _multicastTimer.Stop();
                _multicastSocket.Close();

                _multicastTimer = null;
                _multicastSocket = null;
            }

            if (_multicastSocket == null && _settings.Multicast) {
                StartMulticasting();
            }

            if (_history == null && _settings.History) {
                StartHistoryTimer();
            }

            if (_history != null && _settings.History) {
                _history.KeepHistory = _settings.KeepHistory;
            }

            if (_history != null && !_settings.History) {
                _historyTimer.Stop();
                _historyTimer = null;
                _history = null;
            }
        }
    }

    void mExitApplication_Click(object sender, EventArgs e) {
        //Call our overridden exit thread core method!
        ExitThreadCore();
    }

    protected override void ExitThreadCore() {
        try {
            _server.Stop();
            _computer.Close();
            _multicastSocket.Close();
        } catch (Exception e) { }

        //Call the base method to exit the application
        base.ExitThreadCore();
    }

    private void mCheckForUpdates_Click(object sender, EventArgs e) {
        new CheckForUpdatesDialog().ShowDialog();
    }

    // Getters for children
    public Computer GetComputer() {
        return _computer;
    }

    public Settings GetSettings() {
        return _settings;
    }

    public DeviceManager GetDeviceManager() {
        return _deviceManager;
    }

    public History GetHistory() {
        return _history;
    }

    public NotificationManager GetNotificationManager() {
        return _notificationManager;
    }

    public void UpdateComputer() {
        _computer.Accept(_visitor);
    }

    public void PushTestNotification(String id) {
        _notificationManager.PushTestNotification(_deviceManager.FindDeviceByID(id));
    }
}