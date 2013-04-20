package com.example.bookshelf;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpHandler {
    private HttpURLConnection mUrlConnection = null;

    public HttpHandler(String urlString)
    {
        URL url;
        try {
            url = new URL(urlString);
            mUrlConnection = (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
