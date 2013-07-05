using OpenHardwareMonitor.Hardware;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

public partial class AddNotification : Form {
    private Notification _output;
    private List<IHardware> _hardware;

    public Notification Output {
        get { return _output; }
    }

    public AddNotification(Computer computer) {
        InitializeComponent();
        button_add.DialogResult = System.Windows.Forms.DialogResult.OK;
        button_close.DialogResult = System.Windows.Forms.DialogResult.Cancel;
        comboBox_condition.SelectedIndex = 0;

        // Fill data
        _hardware = new List<IHardware>();

        foreach (IHardware hardware in computer.Hardware) {
            HardwareType type = hardware.HardwareType;
            if (type == HardwareType.CPU || type == HardwareType.GpuAti || type == HardwareType.GpuNvidia) {
                _hardware.Add(hardware);
            } else if (type == HardwareType.Mainboard) {
                type = hardware.SubHardware[0].HardwareType;
                if (type == HardwareType.SuperIO) {
                    _hardware.Add(hardware.SubHardware[0]);
                }
            }
        }

        // Add data to comboboxes
        foreach (IHardware hardware in _hardware) {
            comboBox_hardware.Items.Add(hardware);
        }
    }

    public void LoadData(Notification data) {
        comboBox_hardware.SelectedItem = data.Hardware;
        comboBox_sensor.SelectedItem = data.Sensor;
        switch (data.Condition) {
            case Condition.LessThan: comboBox_condition.SelectedItem = "=<"; break;
            case Condition.MoreThan: comboBox_condition.SelectedItem = ">="; break;
        }
        numeric_Value.Value = data.Value;
    }

    private void button_add_Click(object sender, EventArgs e) {
        // Collect data
        IHardware selectedHardware = (IHardware) comboBox_hardware.SelectedItem;
        ISensor selectedSensor = (ISensor) comboBox_sensor.SelectedItem;
        Condition selectedCondition;
        if (((String) comboBox_condition.SelectedItem) == ">=") {
            selectedCondition = Condition.MoreThan;
        } else {
            selectedCondition = Condition.LessThan;
        }
        decimal value = numeric_Value.Value;

        // Pass data
        _output = new Notification(selectedHardware, selectedSensor, selectedCondition, value);

        // Close form
        Close();
    }

    private void comboBox_hardware_SelectedIndexChanged(object sender, EventArgs e) {
        ISensor[] sensors = ((IHardware) comboBox_hardware.SelectedItem).Sensors;

        comboBox_sensor.SelectedIndex = -1;
        comboBox_sensor.Items.Clear();
        foreach (ISensor sensor in sensors) {
            comboBox_sensor.Items.Add(sensor);
        }
    }
}