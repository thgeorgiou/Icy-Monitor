using System.Net;
using System.Text;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.Windows.Forms;

public class RegisterHttpRequestHandler : HttpRequestHandler {
    public const string NAME = "/register";

    private DeviceManager _deviceManager;

    public RegisterHttpRequestHandler( DeviceManager manager) {
        _deviceManager = manager;
    }

    public void Handle(HttpListenerContext context) {
        HttpListenerResponse response = context.Response;

        // Check if we need to ask the user for registration
        Result result;
        if (context.Request.QueryString["ask"] != null && context.Request.QueryString["ask"].ToLower() == "true") {
            result = Result.AskUser;
        } else {
            result = Result.DoNothing;
        }

        // Create device object
        string name = context.Request.QueryString["name"];
        string id = context.Request.QueryString["id"];
        string gcm = context.Request.QueryString["gcm"];

        // Create response
        string message = "{\"Version\":\"2.0.1\",";

        // Register device if it's not ready
        Device possibleDevice = _deviceManager.FindDeviceByID(id);
        if (_deviceManager.AuthenticationEnabled) {
            if (possibleDevice == null) {
                message += "\"Status\":\"WAITING\"}";
            } else {
                result = Result.DoNothing;
                if (possibleDevice.Allowed == true) message += "\"Status\":\"ALLOWED\"}";
                else message += "\"Status\":\"DENIED\"}";
            }
        } else {
            message += "\"Status\":\"ALLOWED\"}";
            if (possibleDevice == null) result = Result.SaveDevice;
            else result = Result.DoNothing;
        }

        // Fill in response body
        byte[] messageBytes = Encoding.Default.GetBytes(message);
        response.OutputStream.Write(messageBytes, 0, messageBytes.Length);
        response.ContentType = "application/json";
        response.StatusCode = (int)HttpStatusCode.OK;

        // Send the HTTP response to the client        
        response.Close();

        // Decide what to do with this device
        switch (result) {
            case Result.AskUser: AskForRegistration(name, id, gcm); break;
            case Result.SaveDevice: _deviceManager.AddDevice(new Device(name, id, gcm, true)); _deviceManager.SaveData(); break;
        }
    }

    public void AskForRegistration(string name, string id, string gcm) {
        DialogResult dialogResult = new RegistrationDialog(name, id, gcm).ShowDialog();

        Device device = null;
        if (dialogResult == DialogResult.OK) {
            device = new Device(name, id, gcm, true);
        } else if (dialogResult == DialogResult.No) {
            device = new Device(name, id, gcm, false);
        }
        _deviceManager.AddDevice(device);
        _deviceManager.SaveData();
    }

    public string GetName() {
        return NAME;
    }

    enum Result { AskUser, SaveDevice, DoNothing }
}