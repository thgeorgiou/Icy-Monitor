partial class AddDevice {
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
            this.textBox_num1 = new System.Windows.Forms.TextBox();
            this.textBox_num2 = new System.Windows.Forms.TextBox();
            this.textBox_num3 = new System.Windows.Forms.TextBox();
            this.textBox_num4 = new System.Windows.Forms.TextBox();
            this.button_verify = new System.Windows.Forms.Button();
            this.button_add = new System.Windows.Forms.Button();
            this.button_close = new System.Windows.Forms.Button();
            this.textBox_name = new System.Windows.Forms.TextBox();
            this.label6 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.statusStrip1 = new System.Windows.Forms.StatusStrip();
            this.toolStripStatusLabel = new System.Windows.Forms.ToolStripStatusLabel();
            this.statusStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // textBox_num1
            // 
            this.textBox_num1.Location = new System.Drawing.Point(52, 35);
            this.textBox_num1.Name = "textBox_num1";
            this.textBox_num1.Size = new System.Drawing.Size(48, 20);
            this.textBox_num1.TabIndex = 2;
            this.textBox_num1.TextChanged += new System.EventHandler(this.textBox_TextChanged);
            // 
            // textBox_num2
            // 
            this.textBox_num2.Location = new System.Drawing.Point(106, 35);
            this.textBox_num2.Name = "textBox_num2";
            this.textBox_num2.Size = new System.Drawing.Size(48, 20);
            this.textBox_num2.TabIndex = 3;
            this.textBox_num2.TextChanged += new System.EventHandler(this.textBox_TextChanged);
            // 
            // textBox_num3
            // 
            this.textBox_num3.Location = new System.Drawing.Point(160, 35);
            this.textBox_num3.Name = "textBox_num3";
            this.textBox_num3.Size = new System.Drawing.Size(48, 20);
            this.textBox_num3.TabIndex = 4;
            this.textBox_num3.TextChanged += new System.EventHandler(this.textBox_TextChanged);
            // 
            // textBox_num4
            // 
            this.textBox_num4.Location = new System.Drawing.Point(214, 35);
            this.textBox_num4.Name = "textBox_num4";
            this.textBox_num4.Size = new System.Drawing.Size(48, 20);
            this.textBox_num4.TabIndex = 5;
            this.textBox_num4.TextChanged += new System.EventHandler(this.textBox_TextChanged);
            // 
            // button_verify
            // 
            this.button_verify.Location = new System.Drawing.Point(268, 7);
            this.button_verify.Name = "button_verify";
            this.button_verify.Size = new System.Drawing.Size(75, 23);
            this.button_verify.TabIndex = 6;
            this.button_verify.Text = "Verify";
            this.button_verify.UseVisualStyleBackColor = true;
            this.button_verify.Click += new System.EventHandler(this.button_verify_Click);
            // 
            // button_add
            // 
            this.button_add.Enabled = false;
            this.button_add.Location = new System.Drawing.Point(268, 33);
            this.button_add.Name = "button_add";
            this.button_add.Size = new System.Drawing.Size(75, 23);
            this.button_add.TabIndex = 7;
            this.button_add.Text = "Add";
            this.button_add.UseVisualStyleBackColor = true;
            this.button_add.Click += new System.EventHandler(this.button_add_Click);
            // 
            // button_close
            // 
            this.button_close.Location = new System.Drawing.Point(268, 60);
            this.button_close.Name = "button_close";
            this.button_close.Size = new System.Drawing.Size(75, 23);
            this.button_close.TabIndex = 8;
            this.button_close.Text = "Close";
            this.button_close.UseVisualStyleBackColor = true;
            // 
            // textBox_name
            // 
            this.textBox_name.Location = new System.Drawing.Point(52, 9);
            this.textBox_name.Name = "textBox_name";
            this.textBox_name.Size = new System.Drawing.Size(210, 20);
            this.textBox_name.TabIndex = 1;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(8, 12);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(38, 13);
            this.label6.TabIndex = 14;
            this.label6.Text = "Name:";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(8, 38);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(21, 13);
            this.label1.TabIndex = 15;
            this.label1.Text = "ID:";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(8, 62);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(245, 13);
            this.label2.TabIndex = 16;
            this.label2.Text = "You can find the ID at Menu -> Setup Notifications";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(8, 75);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(145, 13);
            this.label3.TabIndex = 17;
            this.label3.Text = "at Icy Monitor on your phone.";
            // 
            // statusStrip1
            // 
            this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripStatusLabel});
            this.statusStrip1.Location = new System.Drawing.Point(0, 95);
            this.statusStrip1.Name = "statusStrip1";
            this.statusStrip1.Size = new System.Drawing.Size(349, 22);
            this.statusStrip1.TabIndex = 18;
            this.statusStrip1.Text = "statusStrip1";
            // 
            // toolStripStatusLabel
            // 
            this.toolStripStatusLabel.Name = "toolStripStatusLabel";
            this.toolStripStatusLabel.Size = new System.Drawing.Size(39, 17);
            this.toolStripStatusLabel.Text = "Ready";
            // 
            // AddDevice
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(349, 117);
            this.Controls.Add(this.statusStrip1);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.textBox_name);
            this.Controls.Add(this.button_close);
            this.Controls.Add(this.button_add);
            this.Controls.Add(this.button_verify);
            this.Controls.Add(this.textBox_num4);
            this.Controls.Add(this.textBox_num3);
            this.Controls.Add(this.textBox_num2);
            this.Controls.Add(this.textBox_num1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Name = "AddDevice";
            this.Text = "Add Device";
            this.statusStrip1.ResumeLayout(false);
            this.statusStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

    }

    #endregion

    private System.Windows.Forms.TextBox textBox_num1;
    private System.Windows.Forms.TextBox textBox_num2;
    private System.Windows.Forms.TextBox textBox_num3;
    private System.Windows.Forms.TextBox textBox_num4;
    private System.Windows.Forms.Button button_verify;
    private System.Windows.Forms.Button button_add;
    private System.Windows.Forms.Button button_close;
    private System.Windows.Forms.TextBox textBox_name;
    private System.Windows.Forms.Label label6;
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.Label label3;
    private System.Windows.Forms.StatusStrip statusStrip1;
    private System.Windows.Forms.ToolStripStatusLabel toolStripStatusLabel;


}