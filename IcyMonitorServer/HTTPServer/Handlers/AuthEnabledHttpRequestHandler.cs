using System.Net;
using System.Text;
using Newtonsoft.Json;
using System.Collections.Generic;

public class AuthEnabledHttpRequesthandler : HttpRequestHandler {
    public const string NAME = "/authEnabled";

    private DeviceManager _deviceManager;

    public AuthEnabledHttpRequesthandler(DeviceManager manager) {
        _deviceManager = manager;
    }

    public void Handle(HttpListenerContext context) {
        HttpListenerResponse response = context.Response;

        // Create response
        string message = "{\"AuthEnabled\":\"" + _deviceManager.AuthenticationEnabled.ToString() + "\"}";
        response.StatusCode = (int)HttpStatusCode.OK;

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