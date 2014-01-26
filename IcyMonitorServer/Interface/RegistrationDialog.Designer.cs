partial class RegistrationDialog {
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(RegistrationDialog));
            this.panel1 = new System.Windows.Forms.Panel();
            this.panel2 = new System.Windows.Forms.Panel();
            this.checkBox_notif = new System.Windows.Forms.CheckBox();
            this.label_id = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label_name = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.button_allo = new System.Windows.Forms.Button();
            this.button_deny = new System.Windows.Forms.Button();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // panel1
            // 
            this.panel1.BackColor = System.Drawing.SystemColors.Control;
            this.panel1.Controls.Add(this.panel2);
            this.panel1.Controls.Add(this.checkBox_notif);
            this.panel1.Controls.Add(this.label_id);
            this.panel1.Controls.Add(this.label4);
            this.panel1.Controls.Add(this.label_name);
            this.panel1.Controls.Add(this.label3);
            this.panel1.Controls.Add(this.label2);
            this.panel1.Controls.Add(this.label1);
            this.panel1.Location = new System.Drawing.Point(-6, -5);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(565, 164);
            this.panel1.TabIndex = 0;
            // 
            // panel2
            // 
            this.panel2.BackColor = System.Drawing.SystemColors.GrayText;
            this.panel2.Location = new System.Drawing.Point(0, 162);
            this.panel2.Name = "panel2";
            this.panel2.Size = new System.Drawing.Size(568, 1);
            this.panel2.TabIndex = 3;
            // 
            // checkBox_notif
            // 
            this.checkBox_notif.AutoSize = true;
            this.checkBox_notif.Enabled = false;
            this.checkBox_notif.Location = new System.Drawing.Point(22, 139);
            this.checkBox_notif.Name = "checkBox_notif";
            this.checkBox_notif.Size = new System.Drawing.Size(130, 17);
            this.checkBox_notif.TabIndex = 6;
            this.checkBox_notif.TabStop = false;
            this.checkBox_notif.Text = "Notification availability";
            this.checkBox_notif.UseVisualStyleBackColor = true;
            // 
            // label_id
            // 
            this.label_id.AutoSize = true;
            this.label_id.Location = new System.Drawing.Point(63, 119);
            this.label_id.Name = "label_id";
            this.label_id.Size = new System.Drawing.Size(0, 13);
            this.label_id.TabIndex = 5;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(22, 119);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(21, 13);
            this.label4.TabIndex = 4;
            this.label4.Text = "ID:";
            // 
            // label_name
            // 
            this.label_name.AutoSize = true;
            this.label_name.Location = new System.Drawing.Point(63, 99);
            this.label_name.Name = "label_name";
            this.label_name.Size = new System.Drawing.Size(0, 13);
            this.label_name.TabIndex = 3;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(19, 99);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(38, 13);
            this.label3.TabIndex = 2;
            this.label3.Text = "Name:";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(19, 48);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(535, 39);
            this.label2.TabIndex = 1;
            this.label2.Text = resources.GetString("label2.Text");
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label1.Location = new System.Drawing.Point(18, 14);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(169, 20);
            this.label1.TabIndex = 0;
            this.label1.Text = "Permission Request";
            // 
            // button_allo
            // 
            this.button_allo.Location = new System.Drawing.Point(389, 165);
            this.button_allo.Name = "button_allo";
            this.button_allo.Size = new System.Drawing.Size(75, 23);
            this.button_allo.TabIndex = 1;
            this.button_allo.Text = "Allow";
            this.button_allo.UseVisualStyleBackColor = true;
            this.button_allo.Click += new System.EventHandler(this.button_allo_Click);
            // 
            // button_deny
            // 
            this.button_deny.Location = new System.Drawing.Point(470, 165);
            this.button_deny.Name = "button_deny";
            this.button_deny.Size = new System.Drawing.Size(75, 23);
            this.button_deny.TabIndex = 2;
            this.button_deny.Text = "Deny";
            this.button_deny.UseVisualStyleBackColor = true;
            this.button_deny.Click += new System.EventHandler(this.button_deny_Click);
            // 
            // RegistrationDialog
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.SystemColors.Window;
            this.ClientSize = new System.Drawing.Size(551, 194);
            this.Controls.Add(this.button_deny);
            this.Controls.Add(this.button_allo);
            this.Controls.Add(this.panel1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "RegistrationDialog";
            this.ShowIcon = false;
            this.Text = "Icy Monitor";
            this.TopMost = true;
            this.Load += new System.EventHandler(this.RegistrationDialog_Load);
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.ResumeLayout(false);

    }

    #endregion

    private System.Windows.Forms.Panel panel1;
    private System.Windows.Forms.CheckBox checkBox_notif;
    private System.Windows.Forms.Label label_id;
    private System.Windows.Forms.Label label4;
    private System.Windows.Forms.Label label_name;
    private System.Windows.Forms.Label label3;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.Button button_allo;
    private System.Windows.Forms.Button button_deny;
    private System.Windows.Forms.Panel panel2;
}