using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using IcyMonitorServer.Properties;
using OpenHardwareMonitor.Hardware;
using IcyMonitorServer;
using Microsoft.Win32;

public class TrayApplicationContext : ApplicationContext {
    #region Private Members
    private System.ComponentModel.IContainer mComponents;   //List of components
    private NotifyIcon mNotifyIcon;
    private ContextMenuStrip mContextMenu;
    private ToolStripMenuItem mDisplayForm;
    private ToolStripMenuItem mExitApplication;
    private ToolStripMenuItem mOpenFirewall;
    private ToolStripMenuItem mRunOnStartup;
    private HttpServer mServer;
    private Computer mComputer;
    #endregion

    public TrayApplicationContext() {
        // Instantiate the component Module to hold everything
        mComponents = new System.ComponentModel.Container();

        // Create the icon
        mNotifyIcon = new NotifyIcon(this.mComponents);
        mNotifyIcon.Icon = Resources.icon_app;
        mNotifyIcon.Text = "Icy Monitor Server";
        mNotifyIcon.Visible = true;

        // Create the context menu and it's items
        mContextMenu = new ContextMenuStrip();
        mDisplayForm = new ToolStripMenuItem();
        mExitApplication = new ToolStripMenuItem();
        mOpenFirewall = new ToolStripMenuItem();
        mRunOnStartup = new ToolStripMenuItem();

        //Attach the menu to the notify icon
        mNotifyIcon.ContextMenuStrip = mContextMenu;

        //Setup the items and add them to the menu strip, adding handlers to be created later
        mDisplayForm.Text = "About";
        mDisplayForm.Click += new EventHandler(mDisplayForm_Click);
        mContextMenu.Items.Add(mDisplayForm);

        mOpenFirewall.Text = "Open Firewall";
        mOpenFirewall.Click += new EventHandler(mOpenFirewall_Click);
        mContextMenu.Items.Add(mOpenFirewall);

        mRunOnStartup.Text = "Run on startup";
        RegistryKey rk = Registry.CurrentUser.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);
        if (rk.GetValue("Icy Monitor Server") != null) { mRunOnStartup.Checked = true; }
        mRunOnStartup.CheckOnClick = true;
        mRunOnStartup.Click += new EventHandler(mRunOnStartup_Click);
        mContextMenu.Items.Add(mRunOnStartup);

        mExitApplication.Text = "Exit";
        mExitApplication.Click += new EventHandler(mExitApplication_Click);
        mContextMenu.Items.Add(mExitApplication);

        // Create server
        mServer = new HttpServer("http://*:28622/");

        // Create computer object from library
        mComputer = new Computer();
        UpdateVisitor updateVisitor = new UpdateVisitor();

        mComputer.CPUEnabled = true;
        mComputer.FanControllerEnabled = true;
        mComputer.GPUEnabled = true;
        mComputer.MainboardEnabled = true;

        mComputer.Open();

        // Add the HttpRequestHandlers
        mServer.AddHttpRequestHandler(new DataHttpRequestHandler(mComputer, updateVisitor));
        mServer.AddHttpRequestHandler(new AboutHttpRequestHandler());
        // mServer.AddHttpRequestHandler(new WebHttpRequestHandler());

        // Start the server
        if (mServer.Start() == false) {
            base.ExitThreadCore();
        }
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
        mServer.Stop();
        mComputer.Close();

        //Call the base method to exit the application
        base.ExitThreadCore();
    }

    void mOpenFirewall_Click(object sender, EventArgs e) {
        System.Diagnostics.Process.Start("netsh", " advfirewall firewall add rule name=\"Icy Monitor Server\" dir=in action=allow protocol=TCP localport=28622");
    }

    private void mRunOnStartup_Click(object sender, EventArgs e) {
        RegistryKey rk = Registry.LocalMachine.OpenSubKey
            ("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);

        if (mRunOnStartup.Checked)
            rk.SetValue("Icy Monitor Server", Application.ExecutablePath.ToString());
        else
            rk.DeleteValue("Icy Monitor Server", false);

    }
}