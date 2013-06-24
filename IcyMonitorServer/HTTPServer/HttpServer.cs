﻿using System.Net;
using System.Text;
using System.Threading;
using System;
using System.Windows.Forms;


public class HttpServer : IDisposable {
    private HttpListener _httpListener = null;
    private Thread _connectionThread = null;
    private Boolean _running, _disposed;

    private HttpResourceLocator _resourceLocator = null;

    public HttpServer(string prefix) {
        if (!HttpListener.IsSupported) {
            // Requires at least a Windows XP with Service Pack 2
            throw new NotSupportedException("The Http Server cannot run on this operating system.");
        }

        _httpListener = new HttpListener();
        // Add the prefixes to listen to
        _httpListener.Prefixes.Add(prefix);

        _resourceLocator = new HttpResourceLocator();

    }

    public void AddHttpRequestHandler(HttpRequestHandler requestHandler) {
        _resourceLocator.AddHttpRequestHandler(requestHandler);
    }

    public bool Start() {
        if (!_httpListener.IsListening) {
            try {
                _httpListener.Start();
            } catch (HttpListenerException e) {
                MessageBox.Show("Cannot bind to port 28622. Maybe the server is already running?\n" + e.Message);
                return false;
            }
            _running = true;
            // Use a thread to listen to the Http requests
            _connectionThread = new Thread(new ThreadStart(this.ConnectionThreadStart));
            _connectionThread.Start();
            return true;
        } else {
            return true;
        }
    }

    public void Stop() {
        if (_httpListener.IsListening) {
            _running = false;
            _httpListener.Stop();
        } // end if httpListener is listening
    } // end public void stop()

    // Action body for _connectionThread
    private void ConnectionThreadStart() {
        try {
            while (_running) {
                // Grab the context and pass it to the HttpResourceLocator to handle it
                HttpListenerContext context = _httpListener.GetContext();
                _resourceLocator.HandleContext(context);

            } // while running
        } catch (HttpListenerException) {
            // This will occurs when the listener gets shutdown.
            Console.WriteLine("HTTP server was shut down.");
        } // end try-catch

    } // end private void connectionThreadStart()

    public virtual void Dispose() {
        this.Dispose(true);
        GC.SuppressFinalize(this);
    } // end public virtual void Dispose()

    private void Dispose(bool disposing) {
        if (this._disposed) {
            return;
        }
        if (disposing) {
            if (this._running) {
                this.Stop();
            }
            if (this._connectionThread != null) {
                this._connectionThread.Abort();
                this._connectionThread = null;
            }
        }
        this._disposed = true;
    } // private void Dispose(bool disposing)

} // end class HttpServer