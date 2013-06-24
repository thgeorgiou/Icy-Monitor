using System.Net;
using System.Text;
using IcyMonitorServer.Properties;

public class WebHttpRequestHandler : HttpRequestHandler {
    public const string NAME = "/";

    public WebHttpRequestHandler() {
    }

    public void Handle(HttpListenerContext context) {

        HttpListenerResponse response = context.Response;
        // Get name from query string
        response.StatusCode = (int) HttpStatusCode.OK;

        // Create page
        string res = context.Request.QueryString["res"];
        string message = "";

        if (res == null) {
            message = Resources.index;
            response.ContentType = "text/html";
        } else {
            if (res == "jquery.js") {
                response.ContentType = "text/javascript";
                message = Resources.jquery;
            } else if (res == "jquery.flot.js") {
                response.ContentType = "text/javascript";
                message = Resources.jquery_flot;
            } else if (res == "main.js") {
                response.ContentType = "text/javascript";
                message = Resources.main;
            }
        }
        // Fill in response body
        byte[] messageBytes = Encoding.Default.GetBytes(message);
        response.OutputStream.Write(messageBytes, 0, messageBytes.Length);

        // Send the HTTP response to the client        
        response.Close();

    } // end public void Handle(HttpListenerContext context)

    public string GetName() {
        return NAME;
    }
} // end public class MorningHttpRequestHandler