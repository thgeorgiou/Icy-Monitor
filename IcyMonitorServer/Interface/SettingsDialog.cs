using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace IcyMonitorServer.Interface {
    public partial class SettingsDialog : Form {
        private DeviceManager _deviceManager;
        private Settings _settings;

        public SettingsDialog(DeviceManager manager, Settings settings) {
            InitializeComponent();

            tabControl.Selected += tabControl_Selected;

            _deviceManager = manager;
            _settings = settings;
        }

        void tabControl_Selected(object sender, TabControlEventArgs e) {
            if (tabControl.SelectedIndex == 1) {
                button_delete.Visible = true;
            } else {
                button_delete.Visible = false;
            }
        }

        private void SettingsDialog_Load(object sender, EventArgs e) {
            numericUpDown_port.Value = _settings.Port;
            textBox_name.Text = _settings.ComputerName;
            checkBox_authdevices.Checked = _settings.AuthorizedDevices;
            checkBox_multicast.Checked = _settings.Multicast;
            checkBox_history.Checked = _settings.History;
            checkBox_keephistory.Checked = _settings.KeepHistory;

            if (!_settings.History) checkBox_keephistory.Enabled = false;

            RegistryKey rk = Registry.CurrentUser.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);
            if (rk.GetValue("Icy Monitor Server") != null) checkBox_startup.Checked = true;

            RefreshList();
        }

        private void RefreshList() {
            deviceBindingSource.Clear();

            int count = _deviceManager.GetCount();
            if (count == 0) {
                button_delete.Enabled = false;
                label_empty_list.Visible = true;
                return;
            }

            for (int i = 0; i < count; i++) {
                Device device = _deviceManager.Get(i);
                deviceBindingSource.Add(device);
            }
        }

        private void button_cancel_Click(object sender, EventArgs e) {
            _deviceManager.SaveData();

            DialogResult = DialogResult.Cancel;
            Dispose();
        }

        private void button_save_Click(object sender, EventArgs e) {
            if (_settings.Port != numericUpDown_port.Value) {
                MessageBox.Show("An application restart is required to change the port.");
            }

            _settings.ComputerName = textBox_name.Text;
            _settings.Port = (int) numericUpDown_port.Value;
            _settings.AuthorizedDevices = checkBox_authdevices.Checked;
            _settings.Multicast = checkBox_multicast.Checked;
            _settings.History = checkBox_history.Checked;
            _settings.KeepHistory = checkBox_keephistory.Checked;

            _deviceManager.SaveData();

            if (checkBox_startup.Checked) {
                Microsoft.Win32.RegistryKey key = Microsoft.Win32.Registry.CurrentUser.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);
                key.SetValue("Icy Monitor Server", Application.ExecutablePath.ToString());
            } else {
                Microsoft.Win32.RegistryKey key = Microsoft.Win32.Registry.CurrentUser.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);
                key.DeleteValue("Icy Monitor Server", false);
            }

            DialogResult = DialogResult.OK;
            Dispose();
        }

        private void button_delete_Click(object sender, EventArgs e) {
            DialogResult dialogResult = MessageBox.Show("Remove device? This cannot be undone!", "Confirm", MessageBoxButtons.YesNo);

            if (dialogResult == DialogResult.Yes) {
                String deviceID = ((Device)dataGridView1.CurrentRow.DataBoundItem).ID;

                _deviceManager.RemoveDeviceByID(deviceID);

                RefreshList();
            }
        }

        private void checkBox_history_CheckedChanged(object sender, EventArgs e) {
            if (checkBox_history.Checked) {
                checkBox_keephistory.Enabled = true;
            } else {
                checkBox_keephistory.Enabled = false;
            }
        }

        private void button_openport_Click(object sender, EventArgs e) {
            DialogResult dialogResult = MessageBox.Show("This will create a rule to open port " + numericUpDown_port.Value + " in Windows Firewall. If you are using another firewall you must add an exception manually.",
            "Open port", MessageBoxButtons.YesNo);

            if (dialogResult == DialogResult.Yes) {
                System.Diagnostics.Process.Start("netsh", " advfirewall firewall add rule name=\"Icy Monitor Server\" dir=in action=allow protocol=TCP localport=" + numericUpDown_port.Value);
                MessageBox.Show("Rule added!");
            } 
        }
    }
}
