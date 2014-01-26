using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Windows.Forms;
using Ionic.Zip;
using System.Diagnostics;


public partial class CheckForUpdatesDialog : Form {
    static String dot = "•";
    static int VersionCode = 3;
    static String VersionString = "v2.0.1";

    JSONUpdate _update;
    Boolean _install = false;

    public CheckForUpdatesDialog() {
        InitializeComponent();
    }

    private void CheckForUpdatesDialog_Load(object sender, EventArgs e) {
        label_version.Text = "Current Version: " + VersionString;

        RefreshData();
    }

    private void RefreshData() {
        richTextBox1.Text += dot + "Checking for updates...\n";

        WebClient client = new WebClient();
        client.DownloadStringCompleted += RefreshCompleted;
        client.DownloadStringAsync(new Uri("http://sds.webs.pm/update.json"));
    }

    void RefreshCompleted(object sender, DownloadStringCompletedEventArgs e) {
        if (!e.Cancelled && e.Error == null) {
            _update = JsonConvert.DeserializeObject<JSONUpdate>(e.Result);

            if (_update.VersionCode > VersionCode || _update.Force) {
                richTextBox1.Text += dot + "A new update is available (version " + _update.Version + "). Changelog:\n";

                if (!_update.Manual) {
                    richTextBox1.Text += _update.Changelog + "\n\n" + dot + "Press \"Download update\" to begin.\n";
                    button_download.Enabled = true;
                } else {
                    richTextBox1.Text += _update.Changelog + "\n\n" + dot + "Automatic update not possible. Please visit the Icy Monitor website to download the update.\n";
                }
            } else {
                richTextBox1.Text += dot + "No new updates available.\n";
            }
        } else {
            richTextBox1.Text += dot + "Could not contact update server.\n";
        }
    }

    private void button_exit_Click(object sender, EventArgs e) {
        this.Dispose();
    }

    private void button_download_Click(object sender, EventArgs e) {
        button_download.Enabled = false;

        if (!_install) {
            if (File.Exists(@"update.zip")) {
                File.Delete(@"update.zip");
            }

            WebClient client = new WebClient();
            client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(UpdateProgressBar);
            client.DownloadFileCompleted += new AsyncCompletedEventHandler(client_DownloadFileCompleted);
            client.DownloadFileAsync(new Uri(_update.Link), @"update.zip");
        } else {
            ProcessStartInfo startInfo = new ProcessStartInfo();
            startInfo.FileName = @"updater.exe";
            Process.Start(startInfo);

            Application.Exit();
        }
    }

    void UpdateProgressBar(object sender, DownloadProgressChangedEventArgs e) {
        double bytesIn = double.Parse(e.BytesReceived.ToString());
        double totalBytes = double.Parse(e.TotalBytesToReceive.ToString());
        double percentage = bytesIn / totalBytes * 100;
        richTextBox1.Text += dot + "Downloaded " + e.BytesReceived + " of " + e.TotalBytesToReceive + "\n";
        progressBar.Value = int.Parse(Math.Truncate(percentage).ToString());
    }

    void client_DownloadFileCompleted(object sender, AsyncCompletedEventArgs e) {
        richTextBox1.Text += dot + "Download finished. Verifying...\n";
        if (_update.md5.ToLower() == GetMD5HashFromFile(@"update.zip").ToLower()) {
            richTextBox1.Text += dot + "OK! Extracting...\n";

            Directory.CreateDirectory(@"update/");

            using (ZipFile zip = ZipFile.Read(@"update.zip")) {
                foreach (ZipEntry f in zip) {
                    f.Extract(@"update/", true);
                }
            }

            richTextBox1.Text += dot + "Extracted. Please click the \"Install update\" button.\n";

            button_download.Enabled = true;
            button_download.Text = "Install update";
            _install = true;
        } else {
            richTextBox1.Text += dot + "Invalid checksum. Installation cancelled.";
        }
    }

    protected string GetMD5HashFromFile(string fileName) {
        FileStream file = new FileStream(fileName, FileMode.Open);
        MD5 md5 = new MD5CryptoServiceProvider();
        byte[] retVal = md5.ComputeHash(file);
        file.Close();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < retVal.Length; i++) {
            sb.Append(retVal[i].ToString("x2"));
        }
        return sb.ToString();
    }

    class JSONUpdate {
        public String Version, Link, Changelog, md5;
        public int VersionCode;
        public bool Force, Manual;
    }
}
