using System.Net;
using System.Text;

public class InvalidHttpRequestHandler : HttpRequestHandler {
    public const string NAME = "/InvalidWebRequestHandler";

    public void Handle(HttpListenerContext context) {
        HttpListenerResponse serverResponse = context.Response;

        // Indicate the failure as a 404 not found
        serverResponse.StatusCode = (int) HttpStatusCode.BadRequest;

        // Send the HTTP response to the client
        serverResponse.Close();
    }

    public string GetName() {
        return NAME;
    }

}