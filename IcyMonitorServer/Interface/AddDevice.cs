using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Text;
using System.Windows.Forms;

public partial class AddDevice : Form {
    private String _regId;
    private Device _output;

    public Device Output {
        get { return _output; }
    }

    public AddDevice() {
        InitializeComponent();
        button_close.DialogResult = System.Windows.Forms.DialogResult.Cancel;
        button_add.DialogResult = System.Windows.Forms.DialogResult.OK;
    }

    private void textBox_TextChanged(object sender, EventArgs e) {
        button_add.Enabled = false;
    }

    private void button_verify_Click(object sender, EventArgs e) {
        toolStripStatusLabel.Text = "Checking with server...";

        Timer bgTimer = new Timer();
        bgTimer.Interval = 1000;
        bgTimer.Tick += bgTimer_Tick;
        bgTimer.Start();
    }

    void bgTimer_Tick(object sender, EventArgs e) {
        ((Timer)sender).Stop();

        WebClient webClient = new WebClient();
        bool success = false;
        String url = "http://icy-monitor-push-server.appspot.com/getID?num1=" + textBox_num1.Text + "&num2=" + textBox_num2.Text + "&num3="
            + textBox_num3.Text + "&num4=" + textBox_num4.Text;
        JObject response = null;
        try {
            response = JObject.Parse(webClient.DownloadString(url));
            success = true;
        } catch (Exception ex) {
            toolStripStatusLabel.Text = "Could not download data. Possible server/connection issue.";
            success = false;
        }

        if (success) {
            String possibleId = (String)response["id"];
            if (possibleId != null && possibleId != "Invalid details") {
                toolStripStatusLabel.Text = "Verified!";
                button_add.Enabled = true;
                _regId = possibleId;
            } else {
                toolStripStatusLabel.Text = "Could not verify.";
            }
        }
    }

    private void button_add_Click(object sender, EventArgs e) {
        _output = new Device(textBox_name.Text, _regId);
    }
}