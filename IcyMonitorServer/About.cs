using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Diagnostics;

public partial class About : Form {
    public About() {
        InitializeComponent();
    }

    private void button_close_Click(object sender, EventArgs e) {
        this.Close();
    }

    private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e) {
        try {
            System.Diagnostics.Process.Start("http://sakisds.github.io/Icy-Monitor/");
        } catch (System.ComponentModel.Win32Exception noBrowser) {
            if (noBrowser.ErrorCode == -2147467259) MessageBox.Show(noBrowser.Message);
        } catch (System.Exception other) {
            MessageBox.Show(other.Message);
        }
			
    }
}