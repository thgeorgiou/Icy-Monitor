public partial class AddNotification {
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
            this.label1 = new System.Windows.Forms.Label();
            this.comboBox_hardware = new System.Windows.Forms.ComboBox();
            this.comboBox_sensor = new System.Windows.Forms.ComboBox();
            this.label2 = new System.Windows.Forms.Label();
            this.comboBox_condition = new System.Windows.Forms.ComboBox();
            this.numeric_Value = new System.Windows.Forms.NumericUpDown();
            this.button_add = new System.Windows.Forms.Button();
            this.button_close = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.numeric_Value)).BeginInit();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(13, 13);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(80, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Notify me when";
            // 
            // comboBox_hardware
            // 
            this.comboBox_hardware.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboBox_hardware.FormattingEnabled = true;
            this.comboBox_hardware.Location = new System.Drawing.Point(99, 10);
            this.comboBox_hardware.Name = "comboBox_hardware";
            this.comboBox_hardware.Size = new System.Drawing.Size(158, 21);
            this.comboBox_hardware.TabIndex = 1;
            this.comboBox_hardware.SelectedIndexChanged += new System.EventHandler(this.comboBox_hardware_SelectedIndexChanged);
            // 
            // comboBox_sensor
            // 
            this.comboBox_sensor.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboBox_sensor.FormattingEnabled = true;
            this.comboBox_sensor.Location = new System.Drawing.Point(263, 10);
            this.comboBox_sensor.Name = "comboBox_sensor";
            this.comboBox_sensor.Size = new System.Drawing.Size(181, 21);
            this.comboBox_sensor.TabIndex = 2;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(450, 13);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(14, 13);
            this.label2.TabIndex = 3;
            this.label2.Text = "is";
            // 
            // comboBox_condition
            // 
            this.comboBox_condition.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboBox_condition.FormattingEnabled = true;
            this.comboBox_condition.Items.AddRange(new object[] {
            ">=",
            "=<"});
            this.comboBox_condition.Location = new System.Drawing.Point(470, 10);
            this.comboBox_condition.Name = "comboBox_condition";
            this.comboBox_condition.Size = new System.Drawing.Size(38, 21);
            this.comboBox_condition.TabIndex = 4;
            // 
            // numeric_Value
            // 
            this.numeric_Value.Location = new System.Drawing.Point(514, 11);
            this.numeric_Value.Name = "numeric_Value";
            this.numeric_Value.Size = new System.Drawing.Size(62, 20);
            this.numeric_Value.TabIndex = 5;
            // 
            // button_add
            // 
            this.button_add.Location = new System.Drawing.Point(501, 37);
            this.button_add.Name = "button_add";
            this.button_add.Size = new System.Drawing.Size(75, 23);
            this.button_add.TabIndex = 6;
            this.button_add.Text = "Add";
            this.button_add.UseVisualStyleBackColor = true;
            this.button_add.Click += new System.EventHandler(this.button_add_Click);
            // 
            // button_close
            // 
            this.button_close.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.button_close.Location = new System.Drawing.Point(420, 37);
            this.button_close.Name = "button_close";
            this.button_close.Size = new System.Drawing.Size(75, 23);
            this.button_close.TabIndex = 7;
            this.button_close.Text = "Close";
            this.button_close.UseVisualStyleBackColor = true;
            // 
            // AddNotification
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(588, 69);
            this.Controls.Add(this.button_close);
            this.Controls.Add(this.button_add);
            this.Controls.Add(this.numeric_Value);
            this.Controls.Add(this.comboBox_condition);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.comboBox_sensor);
            this.Controls.Add(this.comboBox_hardware);
            this.Controls.Add(this.label1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Name = "AddNotification";
            this.Text = "Add Notification";
            ((System.ComponentModel.ISupportInitialize)(this.numeric_Value)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

    }

    #endregion

    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.ComboBox comboBox_hardware;
    private System.Windows.Forms.ComboBox comboBox_sensor;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.ComboBox comboBox_condition;
    private System.Windows.Forms.NumericUpDown numeric_Value;
    private System.Windows.Forms.Button button_add;
    private System.Windows.Forms.Button button_close;
}