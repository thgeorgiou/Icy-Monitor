partial class About {
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
    	System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(About));
    	this.label1 = new System.Windows.Forms.Label();
    	this.textBox1 = new System.Windows.Forms.TextBox();
    	this.button_close = new System.Windows.Forms.Button();
    	this.linkLabel1 = new System.Windows.Forms.LinkLabel();
    	this.SuspendLayout();
    	// 
    	// label1
    	// 
    	this.label1.AutoSize = true;
    	this.label1.Location = new System.Drawing.Point(12, 9);
    	this.label1.Name = "label1";
    	this.label1.Size = new System.Drawing.Size(111, 13);
    	this.label1.TabIndex = 0;
    	this.label1.Text = "Icy Monitor Server 1.4";
    	// 
    	// textBox1
    	// 
    	this.textBox1.Location = new System.Drawing.Point(15, 25);
    	this.textBox1.Multiline = true;
    	this.textBox1.Name = "textBox1";
    	this.textBox1.ReadOnly = true;
    	this.textBox1.Size = new System.Drawing.Size(428, 192);
    	this.textBox1.TabIndex = 1;
    	this.textBox1.Text = resources.GetString("textBox1.Text");
    	// 
    	// button_close
    	// 
    	this.button_close.Location = new System.Drawing.Point(369, 226);
    	this.button_close.Name = "button_close";
    	this.button_close.Size = new System.Drawing.Size(75, 23);
    	this.button_close.TabIndex = 2;
    	this.button_close.Text = "Close";
    	this.button_close.UseVisualStyleBackColor = true;
    	this.button_close.Click += new System.EventHandler(this.button_close_Click);
    	// 
    	// linkLabel1
    	// 
    	this.linkLabel1.AutoSize = true;
    	this.linkLabel1.Location = new System.Drawing.Point(12, 231);
    	this.linkLabel1.Name = "linkLabel1";
    	this.linkLabel1.Size = new System.Drawing.Size(96, 13);
    	this.linkLabel1.TabIndex = 3;
    	this.linkLabel1.TabStop = true;
    	this.linkLabel1.Text = "Find me on Github!";
    	this.linkLabel1.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLabel1_LinkClicked);
    	// 
    	// About
    	// 
    	this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
    	this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
    	this.ClientSize = new System.Drawing.Size(460, 261);
    	this.Controls.Add(this.linkLabel1);
    	this.Controls.Add(this.button_close);
    	this.Controls.Add(this.textBox1);
    	this.Controls.Add(this.label1);
    	this.Name = "About";
    	this.Text = "About";
    	this.ResumeLayout(false);
    	this.PerformLayout();
    }

    #endregion

    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.TextBox textBox1;
    private System.Windows.Forms.Button button_close;
    private System.Windows.Forms.LinkLabel linkLabel1;
}