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

        // Print a message to console indicate invalid request as well
        //Console.WriteLine("Invalid request from client. Request string: "
        //    + context.Request.RawUrl);
    } // end public void handle(HttpListenerContext context)

    public string GetName() {
        return NAME;
    } // end public string GetName()

} // end public class InvalidHttpRequestHandler