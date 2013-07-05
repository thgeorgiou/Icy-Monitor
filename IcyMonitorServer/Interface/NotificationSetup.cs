using Newtonsoft.Json;
using OpenHardwareMonitor.Hardware;
using PushSharp;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;

public partial class NotificationSetup : Form {
    private Computer _computer;
    private List<Notification> _data_notif = new List<Notification>();
    private List<Device> _data_device = new List<Device>();
    private Settings _settings;

    public NotificationSetup(Computer computer, List<Notification> dataNotif, List<Device> dataDevice, Settings settings) {
        _computer = computer;
        _data_notif = dataNotif;
        _data_device = dataDevice;
        _settings = settings;
        InitializeComponent();
    }

    private void RefreshList() {
        listBox_notifications.Items.Clear();
        foreach (Notification data in _data_notif) {
            listBox_notifications.Items.Add(data);
        }
        listBox_devices.Items.Clear();
        foreach (Device device in _data_device) {
            listBox_devices.Items.Add(device);
        }
    }

    private void button_notif_add_Click(object sender, EventArgs e) {
        AddNotification form = new AddNotification(_computer);
        if (form.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
            _data_notif.Add(form.Output);
            RefreshList();
        }
    }

    private void button_notif_edit_Click(object sender, EventArgs e) {
        if (listBox_notifications.SelectedItem == null) {
            MessageBox.Show("Select something first.");
        } else {
            AddNotification form = new AddNotification(_computer);
            Notification edit = (Notification)listBox_notifications.SelectedItem;
            form.LoadData(edit);
            if (form.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
                _data_notif.Remove(edit);
                _data_notif.Add(form.Output);
                RefreshList();
            }
        }
    }

    private void button_notif_remove_Click(object sender, EventArgs e) {
        _data_notif.Remove((Notification) listBox_notifications.SelectedItem);
        RefreshList();
    }

    private void NotificationSetup_FormClosed(object sender, FormClosedEventArgs e) {
        List<NotificationSerializable> serializable_notif = new List<NotificationSerializable>();

        foreach (Notification notif in _data_notif) {
            serializable_notif.Add(notif.GetSerializable());
        }

        Serializer.SerializeObject<List<NotificationSerializable>>(serializable_notif, "notif.dat");
        Serializer.SerializeObject<List<Device>>(_data_device, "devices.dat");

        _settings.ComputerName = text_compname.Text;
        _settings.RefreshRate = (int)numeric_refreshrate.Value;

        Serializer.SerializeObject<Settings>(_settings, "settings.dat");
    }

    private void NotificationSetup_Load(object sender, EventArgs e) {
        RefreshList();
        numeric_refreshrate.Minimum = 1000;
        numeric_refreshrate.Maximum = 300000;
        numeric_refreshrate.Value = _settings.RefreshRate;
        text_compname.Text = _settings.ComputerName;
    }

    private void button_devices_add_Click(object sender, EventArgs e) {
        AddDevice dialog = new AddDevice();
        if (dialog.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
            _data_device.Add(dialog.Output);
            RefreshList();
        }
    }

    private void button_devices_remove_Click(object sender, EventArgs e) {
        _data_device.Remove((Device)listBox_devices.SelectedItem);
        RefreshList();
    }

    private void button_test_Click(object sender, EventArgs e) {
        Device device = (Device) listBox_devices.SelectedItem;
        if (device == null) {
            MessageBox.Show("Select something first!");
        } else {
            PushBroker push = new PushBroker();
            String text = JsonConvert.SerializeObject(new JSONNotification("Icy Monitor", "Test", "Test"));
            device.sendNotification(push, text);
            push.StopAllServices();
        }
    }

    private void button_default_Click(object sender, EventArgs e) {
        numeric_refreshrate.Value = 60000;
    }
}