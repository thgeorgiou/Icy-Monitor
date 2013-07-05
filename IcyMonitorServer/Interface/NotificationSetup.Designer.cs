partial class NotificationSetup
{
    /// <summary>
    /// Required designer variable.
    /// </summary>
    private System.ComponentModel.IContainer components = null;

    /// <summary>
    /// Clean up any resources being used.
    /// </summary>
    /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
    protected override void Dispose(bool disposing)
    {
        if (disposing && (components != null))
        {
            components.Dispose();
        }
        base.Dispose(disposing);
    }

    #region Windows Form Designer generated code

    /// <summary>
    /// Required method for Designer support - do not modify
    /// the contents of this method with the code editor.
    /// </summary>
    private void InitializeComponent()
    {
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.button_notif_remove = new System.Windows.Forms.Button();
            this.button_notif_edit = new System.Windows.Forms.Button();
            this.button_notif_add = new System.Windows.Forms.Button();
            this.listBox_notifications = new System.Windows.Forms.ListBox();
            this.tabPage2 = new System.Windows.Forms.TabPage();
            this.button_test = new System.Windows.Forms.Button();
            this.button_devices_remove = new System.Windows.Forms.Button();
            this.button_devices_add = new System.Windows.Forms.Button();
            this.listBox_devices = new System.Windows.Forms.ListBox();
            this.tabPage3 = new System.Windows.Forms.TabPage();
            this.text_compname = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.button_default = new System.Windows.Forms.Button();
            this.numeric_refreshrate = new System.Windows.Forms.NumericUpDown();
            this.label1 = new System.Windows.Forms.Label();
            this.tabControl1.SuspendLayout();
            this.tabPage1.SuspendLayout();
            this.tabPage2.SuspendLayout();
            this.tabPage3.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numeric_refreshrate)).BeginInit();
            this.SuspendLayout();
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Controls.Add(this.tabPage2);
            this.tabControl1.Controls.Add(this.tabPage3);
            this.tabControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabControl1.Location = new System.Drawing.Point(0, 0);
            this.tabControl1.Margin = new System.Windows.Forms.Padding(6);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(384, 340);
            this.tabControl1.TabIndex = 0;
            // 
            // tabPage1
            // 
            this.tabPage1.Controls.Add(this.button_notif_remove);
            this.tabPage1.Controls.Add(this.button_notif_edit);
            this.tabPage1.Controls.Add(this.button_notif_add);
            this.tabPage1.Controls.Add(this.listBox_notifications);
            this.tabPage1.Location = new System.Drawing.Point(4, 22);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage1.Size = new System.Drawing.Size(376, 314);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "Notifications";
            this.tabPage1.UseVisualStyleBackColor = true;
            // 
            // button_notif_remove
            // 
            this.button_notif_remove.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button_notif_remove.Location = new System.Drawing.Point(293, 64);
            this.button_notif_remove.Name = "button_notif_remove";
            this.button_notif_remove.Size = new System.Drawing.Size(75, 23);
            this.button_notif_remove.TabIndex = 3;
            this.button_notif_remove.Text = "Remove";
            this.button_notif_remove.UseVisualStyleBackColor = true;
            this.button_notif_remove.Click += new System.EventHandler(this.button_notif_remove_Click);
            // 
            // button_notif_edit
            // 
            this.button_notif_edit.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button_notif_edit.Location = new System.Drawing.Point(293, 35);
            this.button_notif_edit.Name = "button_notif_edit";
            this.button_notif_edit.Size = new System.Drawing.Size(75, 23);
            this.button_notif_edit.TabIndex = 2;
            this.button_notif_edit.Text = "Edit";
            this.button_notif_edit.UseVisualStyleBackColor = true;
            this.button_notif_edit.Click += new System.EventHandler(this.button_notif_edit_Click);
            // 
            // button_notif_add
            // 
            this.button_notif_add.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button_notif_add.Location = new System.Drawing.Point(293, 6);
            this.button_notif_add.Name = "button_notif_add";
            this.button_notif_add.Size = new System.Drawing.Size(75, 23);
            this.button_notif_add.TabIndex = 1;
            this.button_notif_add.Text = "Add";
            this.button_notif_add.UseVisualStyleBackColor = true;
            this.button_notif_add.Click += new System.EventHandler(this.button_notif_add_Click);
            // 
            // listBox_notifications
            // 
            this.listBox_notifications.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.listBox_notifications.FormattingEnabled = true;
            this.listBox_notifications.Location = new System.Drawing.Point(6, 6);
            this.listBox_notifications.Name = "listBox_notifications";
            this.listBox_notifications.Size = new System.Drawing.Size(281, 303);
            this.listBox_notifications.TabIndex = 0;
            // 
            // tabPage2
            // 
            this.tabPage2.Controls.Add(this.button_test);
            this.tabPage2.Controls.Add(this.button_devices_remove);
            this.tabPage2.Controls.Add(this.button_devices_add);
            this.tabPage2.Controls.Add(this.listBox_devices);
            this.tabPage2.Location = new System.Drawing.Point(4, 22);
            this.tabPage2.Name = "tabPage2";
            this.tabPage2.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage2.Size = new System.Drawing.Size(376, 314);
            this.tabPage2.TabIndex = 1;
            this.tabPage2.Text = "Devices";
            this.tabPage2.UseVisualStyleBackColor = true;
            // 
            // button_test
            // 
            this.button_test.Location = new System.Drawing.Point(293, 64);
            this.button_test.Name = "button_test";
            this.button_test.Size = new System.Drawing.Size(75, 23);
            this.button_test.TabIndex = 8;
            this.button_test.Text = "Test";
            this.button_test.UseVisualStyleBackColor = true;
            this.button_test.Click += new System.EventHandler(this.button_test_Click);
            // 
            // button_devices_remove
            // 
            this.button_devices_remove.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button_devices_remove.Location = new System.Drawing.Point(293, 35);
            this.button_devices_remove.Name = "button_devices_remove";
            this.button_devices_remove.Size = new System.Drawing.Size(75, 23);
            this.button_devices_remove.TabIndex = 7;
            this.button_devices_remove.Text = "Remove";
            this.button_devices_remove.UseVisualStyleBackColor = true;
            this.button_devices_remove.Click += new System.EventHandler(this.button_devices_remove_Click);
            // 
            // button_devices_add
            // 
            this.button_devices_add.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button_devices_add.Location = new System.Drawing.Point(293, 6);
            this.button_devices_add.Name = "button_devices_add";
            this.button_devices_add.Size = new System.Drawing.Size(75, 23);
            this.button_devices_add.TabIndex = 5;
            this.button_devices_add.Text = "Add";
            this.button_devices_add.UseVisualStyleBackColor = true;
            this.button_devices_add.Click += new System.EventHandler(this.button_devices_add_Click);
            // 
            // listBox_devices
            // 
            this.listBox_devices.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.listBox_devices.FormattingEnabled = true;
            this.listBox_devices.Location = new System.Drawing.Point(6, 6);
            this.listBox_devices.Name = "listBox_devices";
            this.listBox_devices.Size = new System.Drawing.Size(281, 303);
            this.listBox_devices.TabIndex = 4;
            // 
            // tabPage3
            // 
            this.tabPage3.Controls.Add(this.text_compname);
            this.tabPage3.Controls.Add(this.label2);
            this.tabPage3.Controls.Add(this.button_default);
            this.tabPage3.Controls.Add(this.numeric_refreshrate);
            this.tabPage3.Controls.Add(this.label1);
            this.tabPage3.Location = new System.Drawing.Point(4, 22);
            this.tabPage3.Name = "tabPage3";
            this.tabPage3.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage3.Size = new System.Drawing.Size(376, 314);
            this.tabPage3.TabIndex = 2;
            this.tabPage3.Text = "Computer";
            this.tabPage3.UseVisualStyleBackColor = true;
            // 
            // text_compname
            // 
            this.text_compname.Location = new System.Drawing.Point(215, 39);
            this.text_compname.Name = "text_compname";
            this.text_compname.Size = new System.Drawing.Size(153, 20);
            this.text_compname.TabIndex = 4;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(5, 42);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(203, 13);
            this.label2.TabIndex = 3;
            this.label2.Text = "Computer Name (appears in notifications):";
            // 
            // button_default
            // 
            this.button_default.Location = new System.Drawing.Point(316, 10);
            this.button_default.Name = "button_default";
            this.button_default.Size = new System.Drawing.Size(52, 23);
            this.button_default.TabIndex = 2;
            this.button_default.Text = "Default";
            this.button_default.UseVisualStyleBackColor = true;
            this.button_default.Click += new System.EventHandler(this.button_default_Click);
            // 
            // numeric_refreshrate
            // 
            this.numeric_refreshrate.Location = new System.Drawing.Point(215, 12);
            this.numeric_refreshrate.Name = "numeric_refreshrate";
            this.numeric_refreshrate.Size = new System.Drawing.Size(95, 20);
            this.numeric_refreshrate.TabIndex = 1;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(5, 14);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(189, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Notification Refresh Rate (in seconds):";
            // 
            // NotificationSetup
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(384, 340);
            this.Controls.Add(this.tabControl1);
            this.Name = "NotificationSetup";
            this.Text = "Push Notification Setup";
            this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.NotificationSetup_FormClosed);
            this.Load += new System.EventHandler(this.NotificationSetup_Load);
            this.tabControl1.ResumeLayout(false);
            this.tabPage1.ResumeLayout(false);
            this.tabPage2.ResumeLayout(false);
            this.tabPage3.ResumeLayout(false);
            this.tabPage3.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numeric_refreshrate)).EndInit();
            this.ResumeLayout(false);

    }

    #endregion

    private System.Windows.Forms.TabControl tabControl1;
    private System.Windows.Forms.TabPage tabPage1;
    private System.Windows.Forms.Button button_notif_remove;
    private System.Windows.Forms.Button button_notif_edit;
    private System.Windows.Forms.Button button_notif_add;
    private System.Windows.Forms.ListBox listBox_notifications;
    private System.Windows.Forms.TabPage tabPage2;
    private System.Windows.Forms.Button button_devices_remove;
    private System.Windows.Forms.Button button_devices_add;
    private System.Windows.Forms.ListBox listBox_devices;
    private System.Windows.Forms.Button button_test;
    private System.Windows.Forms.TabPage tabPage3;
    private System.Windows.Forms.TextBox text_compname;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.Button button_default;
    private System.Windows.Forms.NumericUpDown numeric_refreshrate;
    private System.Windows.Forms.Label label1;
}