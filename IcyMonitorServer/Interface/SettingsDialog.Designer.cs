namespace IcyMonitorServer.Interface {
    partial class SettingsDialog {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing) {
            if (disposing && (components != null)) {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent() {
            this.components = new System.ComponentModel.Container();
            this.tabControl = new System.Windows.Forms.TabControl();
            this.tabPageSettings = new System.Windows.Forms.TabPage();
            this.checkBox_startup = new System.Windows.Forms.CheckBox();
            this.button_openport = new System.Windows.Forms.Button();
            this.checkBox_keephistory = new System.Windows.Forms.CheckBox();
            this.checkBox_history = new System.Windows.Forms.CheckBox();
            this.checkBox_multicast = new System.Windows.Forms.CheckBox();
            this.checkBox_authdevices = new System.Windows.Forms.CheckBox();
            this.numericUpDown_port = new System.Windows.Forms.NumericUpDown();
            this.label3 = new System.Windows.Forms.Label();
            this.textBox_name = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.tabPageDevices = new System.Windows.Forms.TabPage();
            this.label_empty_list = new System.Windows.Forms.Label();
            this.dataGridView1 = new System.Windows.Forms.DataGridView();
            this.dataGridViewCheckBoxColumn1 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
            this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.deviceBindingSource = new System.Windows.Forms.BindingSource(this.components);
            this.button_save = new System.Windows.Forms.Button();
            this.button_cancel = new System.Windows.Forms.Button();
            this.button_delete = new System.Windows.Forms.Button();
            this.tabControl.SuspendLayout();
            this.tabPageSettings.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDown_port)).BeginInit();
            this.tabPageDevices.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dataGridView1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.deviceBindingSource)).BeginInit();
            this.SuspendLayout();
            // 
            // tabControl
            // 
            this.tabControl.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tabControl.Controls.Add(this.tabPageSettings);
            this.tabControl.Controls.Add(this.tabPageDevices);
            this.tabControl.Location = new System.Drawing.Point(2, 2);
            this.tabControl.Name = "tabControl";
            this.tabControl.SelectedIndex = 0;
            this.tabControl.Size = new System.Drawing.Size(326, 197);
            this.tabControl.TabIndex = 0;
            // 
            // tabPageSettings
            // 
            this.tabPageSettings.Controls.Add(this.checkBox_startup);
            this.tabPageSettings.Controls.Add(this.button_openport);
            this.tabPageSettings.Controls.Add(this.checkBox_keephistory);
            this.tabPageSettings.Controls.Add(this.checkBox_history);
            this.tabPageSettings.Controls.Add(this.checkBox_multicast);
            this.tabPageSettings.Controls.Add(this.checkBox_authdevices);
            this.tabPageSettings.Controls.Add(this.numericUpDown_port);
            this.tabPageSettings.Controls.Add(this.label3);
            this.tabPageSettings.Controls.Add(this.textBox_name);
            this.tabPageSettings.Controls.Add(this.label1);
            this.tabPageSettings.Location = new System.Drawing.Point(4, 22);
            this.tabPageSettings.Name = "tabPageSettings";
            this.tabPageSettings.Padding = new System.Windows.Forms.Padding(3);
            this.tabPageSettings.Size = new System.Drawing.Size(318, 171);
            this.tabPageSettings.TabIndex = 0;
            this.tabPageSettings.Text = "Settings";
            this.tabPageSettings.UseVisualStyleBackColor = true;
            // 
            // checkBox_startup
            // 
            this.checkBox_startup.AutoSize = true;
            this.checkBox_startup.Location = new System.Drawing.Point(10, 148);
            this.checkBox_startup.Name = "checkBox_startup";
            this.checkBox_startup.Size = new System.Drawing.Size(142, 17);
            this.checkBox_startup.TabIndex = 12;
            this.checkBox_startup.Text = "Run at Windows Startup";
            this.checkBox_startup.UseVisualStyleBackColor = true;
            // 
            // button_openport
            // 
            this.button_openport.Location = new System.Drawing.Point(224, 28);
            this.button_openport.Name = "button_openport";
            this.button_openport.Size = new System.Drawing.Size(88, 24);
            this.button_openport.TabIndex = 11;
            this.button_openport.Text = "Open port...";
            this.button_openport.UseVisualStyleBackColor = true;
            this.button_openport.Click += new System.EventHandler(this.button_openport_Click);
            // 
            // checkBox_keephistory
            // 
            this.checkBox_keephistory.AutoSize = true;
            this.checkBox_keephistory.Location = new System.Drawing.Point(10, 125);
            this.checkBox_keephistory.Name = "checkBox_keephistory";
            this.checkBox_keephistory.Size = new System.Drawing.Size(159, 17);
            this.checkBox_keephistory.TabIndex = 10;
            this.checkBox_keephistory.Text = "Keep old history files on disk";
            this.checkBox_keephistory.UseVisualStyleBackColor = true;
            // 
            // checkBox_history
            // 
            this.checkBox_history.AutoSize = true;
            this.checkBox_history.Location = new System.Drawing.Point(10, 102);
            this.checkBox_history.Name = "checkBox_history";
            this.checkBox_history.Size = new System.Drawing.Size(94, 17);
            this.checkBox_history.TabIndex = 9;
            this.checkBox_history.Text = "Enable History";
            this.checkBox_history.UseVisualStyleBackColor = true;
            this.checkBox_history.CheckedChanged += new System.EventHandler(this.checkBox_history_CheckedChanged);
            // 
            // checkBox_multicast
            // 
            this.checkBox_multicast.AutoSize = true;
            this.checkBox_multicast.Location = new System.Drawing.Point(10, 79);
            this.checkBox_multicast.Name = "checkBox_multicast";
            this.checkBox_multicast.Size = new System.Drawing.Size(233, 17);
            this.checkBox_multicast.TabIndex = 8;
            this.checkBox_multicast.Text = "Enable UDP Multicasting (for autodetection)";
            this.checkBox_multicast.UseVisualStyleBackColor = true;
            // 
            // checkBox_authdevices
            // 
            this.checkBox_authdevices.AutoSize = true;
            this.checkBox_authdevices.Location = new System.Drawing.Point(10, 56);
            this.checkBox_authdevices.Name = "checkBox_authdevices";
            this.checkBox_authdevices.Size = new System.Drawing.Size(165, 17);
            this.checkBox_authdevices.TabIndex = 7;
            this.checkBox_authdevices.Text = "Allow only authorized devices";
            this.checkBox_authdevices.UseVisualStyleBackColor = true;
            // 
            // numericUpDown_port
            // 
            this.numericUpDown_port.Location = new System.Drawing.Point(103, 30);
            this.numericUpDown_port.Maximum = new decimal(new int[] {
            28999,
            0,
            0,
            0});
            this.numericUpDown_port.Minimum = new decimal(new int[] {
            5000,
            0,
            0,
            0});
            this.numericUpDown_port.Name = "numericUpDown_port";
            this.numericUpDown_port.Size = new System.Drawing.Size(115, 20);
            this.numericUpDown_port.TabIndex = 5;
            this.numericUpDown_port.Value = new decimal(new int[] {
            28622,
            0,
            0,
            0});
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(7, 32);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(63, 13);
            this.label3.TabIndex = 4;
            this.label3.Text = "Server Port:";
            // 
            // textBox_name
            // 
            this.textBox_name.Location = new System.Drawing.Point(103, 4);
            this.textBox_name.Name = "textBox_name";
            this.textBox_name.Size = new System.Drawing.Size(209, 20);
            this.textBox_name.TabIndex = 1;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(7, 7);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(89, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Computer Name: ";
            // 
            // tabPageDevices
            // 
            this.tabPageDevices.Controls.Add(this.label_empty_list);
            this.tabPageDevices.Controls.Add(this.dataGridView1);
            this.tabPageDevices.Location = new System.Drawing.Point(4, 22);
            this.tabPageDevices.Name = "tabPageDevices";
            this.tabPageDevices.Padding = new System.Windows.Forms.Padding(3);
            this.tabPageDevices.Size = new System.Drawing.Size(318, 171);
            this.tabPageDevices.TabIndex = 2;
            this.tabPageDevices.Text = "Devices";
            this.tabPageDevices.UseVisualStyleBackColor = true;
            // 
            // label_empty_list
            // 
            this.label_empty_list.AutoSize = true;
            this.label_empty_list.Location = new System.Drawing.Point(54, 75);
            this.label_empty_list.Name = "label_empty_list";
            this.label_empty_list.Size = new System.Drawing.Size(196, 39);
            this.label_empty_list.TabIndex = 1;
            this.label_empty_list.Text = "                No devices added\r\nYou can close this window and connect\r\nto the s" +
    "erver using your android device";
            this.label_empty_list.Visible = false;
            // 
            // dataGridView1
            // 
            this.dataGridView1.AllowUserToAddRows = false;
            this.dataGridView1.AllowUserToDeleteRows = false;
            this.dataGridView1.AllowUserToResizeColumns = false;
            this.dataGridView1.AllowUserToResizeRows = false;
            this.dataGridView1.AutoGenerateColumns = false;
            this.dataGridView1.BackgroundColor = System.Drawing.SystemColors.ControlLightLight;
            this.dataGridView1.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.dataGridView1.ClipboardCopyMode = System.Windows.Forms.DataGridViewClipboardCopyMode.Disable;
            this.dataGridView1.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataGridView1.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dataGridViewCheckBoxColumn1,
            this.dataGridViewTextBoxColumn1,
            this.dataGridViewTextBoxColumn2});
            this.dataGridView1.DataSource = this.deviceBindingSource;
            this.dataGridView1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dataGridView1.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnKeystroke;
            this.dataGridView1.Location = new System.Drawing.Point(3, 3);
            this.dataGridView1.Margin = new System.Windows.Forms.Padding(0);
            this.dataGridView1.MultiSelect = false;
            this.dataGridView1.Name = "dataGridView1";
            this.dataGridView1.RowHeadersVisible = false;
            this.dataGridView1.RowHeadersWidthSizeMode = System.Windows.Forms.DataGridViewRowHeadersWidthSizeMode.DisableResizing;
            this.dataGridView1.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dataGridView1.ShowCellErrors = false;
            this.dataGridView1.ShowCellToolTips = false;
            this.dataGridView1.ShowEditingIcon = false;
            this.dataGridView1.ShowRowErrors = false;
            this.dataGridView1.Size = new System.Drawing.Size(312, 165);
            this.dataGridView1.TabIndex = 0;
            // 
            // dataGridViewCheckBoxColumn1
            // 
            this.dataGridViewCheckBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.ColumnHeader;
            this.dataGridViewCheckBoxColumn1.DataPropertyName = "Allowed";
            this.dataGridViewCheckBoxColumn1.HeaderText = "Allowed";
            this.dataGridViewCheckBoxColumn1.Name = "dataGridViewCheckBoxColumn1";
            this.dataGridViewCheckBoxColumn1.Width = 48;
            // 
            // dataGridViewTextBoxColumn1
            // 
            this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
            this.dataGridViewTextBoxColumn1.HeaderText = "Name";
            this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
            // 
            // dataGridViewTextBoxColumn2
            // 
            this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.dataGridViewTextBoxColumn2.DataPropertyName = "ID";
            this.dataGridViewTextBoxColumn2.HeaderText = "ID";
            this.dataGridViewTextBoxColumn2.MinimumWidth = 30;
            this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
            // 
            // deviceBindingSource
            // 
            this.deviceBindingSource.AllowNew = true;
            this.deviceBindingSource.DataSource = typeof(Device);
            // 
            // button_save
            // 
            this.button_save.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.button_save.Location = new System.Drawing.Point(253, 201);
            this.button_save.Name = "button_save";
            this.button_save.Size = new System.Drawing.Size(75, 23);
            this.button_save.TabIndex = 1;
            this.button_save.Text = "OK";
            this.button_save.UseVisualStyleBackColor = true;
            this.button_save.Click += new System.EventHandler(this.button_save_Click);
            // 
            // button_cancel
            // 
            this.button_cancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.button_cancel.Location = new System.Drawing.Point(172, 201);
            this.button_cancel.Name = "button_cancel";
            this.button_cancel.Size = new System.Drawing.Size(75, 23);
            this.button_cancel.TabIndex = 2;
            this.button_cancel.Text = "Cancel";
            this.button_cancel.UseVisualStyleBackColor = true;
            this.button_cancel.Click += new System.EventHandler(this.button_cancel_Click);
            // 
            // button_delete
            // 
            this.button_delete.Location = new System.Drawing.Point(9, 201);
            this.button_delete.Name = "button_delete";
            this.button_delete.Size = new System.Drawing.Size(75, 23);
            this.button_delete.TabIndex = 3;
            this.button_delete.Text = "Remove";
            this.button_delete.UseVisualStyleBackColor = true;
            this.button_delete.Visible = false;
            this.button_delete.Click += new System.EventHandler(this.button_delete_Click);
            // 
            // SettingsDialog
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(332, 229);
            this.Controls.Add(this.button_delete);
            this.Controls.Add(this.button_cancel);
            this.Controls.Add(this.button_save);
            this.Controls.Add(this.tabControl);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.MaximizeBox = false;
            this.Name = "SettingsDialog";
            this.ShowInTaskbar = false;
            this.Text = "Settings";
            this.Load += new System.EventHandler(this.SettingsDialog_Load);
            this.tabControl.ResumeLayout(false);
            this.tabPageSettings.ResumeLayout(false);
            this.tabPageSettings.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDown_port)).EndInit();
            this.tabPageDevices.ResumeLayout(false);
            this.tabPageDevices.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dataGridView1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.deviceBindingSource)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TabControl tabControl;
        private System.Windows.Forms.TabPage tabPageSettings;
        private System.Windows.Forms.Button button_save;
        private System.Windows.Forms.Button button_cancel;
        private System.Windows.Forms.NumericUpDown numericUpDown_port;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox textBox_name;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.CheckBox checkBox_authdevices;
        private System.Windows.Forms.CheckBox checkBox_multicast;
        private System.Windows.Forms.TabPage tabPageDevices;
        private System.Windows.Forms.DataGridViewCheckBoxColumn allowedDataGridViewCheckBoxColumn;
        private System.Windows.Forms.DataGridViewTextBoxColumn nameDataGridViewTextBoxColumn;
        private System.Windows.Forms.DataGridViewTextBoxColumn iDDataGridViewTextBoxColumn;
        private System.Windows.Forms.Button button_delete;
        private System.Windows.Forms.CheckBox checkBox_history;
        private System.Windows.Forms.CheckBox checkBox_keephistory;
        private System.Windows.Forms.Label label_empty_list;
        private System.Windows.Forms.BindingSource deviceBindingSource;
        private System.Windows.Forms.DataGridView dataGridView1;
        private System.Windows.Forms.DataGridViewCheckBoxColumn dataGridViewCheckBoxColumn1;
        private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
        private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
        private System.Windows.Forms.Button button_openport;
        private System.Windows.Forms.CheckBox checkBox_startup;
    }
}