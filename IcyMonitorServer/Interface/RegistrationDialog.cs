using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

public partial class RegistrationDialog : Form {
    public RegistrationDialog(String name, String id, String gcm) {
        InitializeComponent();

        label_name.Text = name;
        label_id.Text = id;
        if (gcm != null) if (gcm != "-1" && gcm != "-2" && gcm != "null") {
            checkBox_notif.Checked = true;
        }
    }

    private void button_allo_Click(object sender, EventArgs e) {
        this.DialogResult = DialogResult.OK;
        this.Dispose();
    }

    private void button_deny_Click(object sender, EventArgs e) {
        this.DialogResult = DialogResult.No;
        this.Dispose();
    }

    private void RegistrationDialog_Load(object sender, EventArgs e) {
        this.Activate();
    }
}

