using System.Net;
using System.Text;
using Newtonsoft.Json;
using System.Collections.Generic;

public class AuthHttpRequestHandler : HttpRequestHandler {
    public const string NAME = "/auth";

    private DeviceManager _deviceManager;

    public AuthHttpRequestHandler(DeviceManager manager) {
        _deviceManager = manager;
    }

    public void Handle(HttpListenerContext context) {
        HttpListenerResponse response = context.Response;
 
        // Get device data
        string id = context.Request.QueryString["id"];
        Device device = _deviceManager.FindDeviceByID(id);

        // Create response
        string message = "{\"Version\":\"2.0.1\", \"Auth\": \"";
        response.StatusCode = (int)HttpStatusCode.OK;

        if (device != null || !_deviceManager.AuthenticationEnabled) {
            message += "OK\"}";
        } else {
            message += "DENIED\"}";
        }

        // Fill in response body
        byte[] messageBytes = Encoding.Default.GetBytes(message);
        response.OutputStream.Write(messageBytes, 0, messageBytes.Length);
        response.ContentType = "application/json";

        // Send the HTTP response to the client        
        response.Close();

    }

    public string GetName() {
        return NAME;
    }
}