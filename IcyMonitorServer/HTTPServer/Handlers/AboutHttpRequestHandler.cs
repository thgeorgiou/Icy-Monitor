using System.Net;
using System.Text;
using Newtonsoft.Json;

public class AboutHttpRequestHandler : HttpRequestHandler {
    public const string NAME = "/about";

    public void Handle(HttpListenerContext context) {
        HttpListenerResponse response = context.Response;
        // Get name from query string
        response.StatusCode = (int) HttpStatusCode.OK;

        // Create message JSON
        string message = JsonConvert.SerializeObject(new JSONAboutData());

        // Fill in response body
        byte[] messageBytes = Encoding.Default.GetBytes(message);
        response.OutputStream.Write(messageBytes, 0, messageBytes.Length);
        response.ContentType = "application/json";

        // Send the HTTP response to the client        
        response.Close();

    } // end public void Handle(HttpListenerContext context)

    public string GetName() {
        return NAME;
    }
} // end public class MorningHttpRequestHandler