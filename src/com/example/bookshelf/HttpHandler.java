package com.example.bookshelf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class HttpHandler {

    private static String URL_GET_ALL_BOOK = "http://108.61.84.203/~bookshel/books.php";
    private static String LOG_TAG = "HttpHandler";
    static public void getAllBooks()
    {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(URL_GET_ALL_BOOK);
            urlConnection = (HttpURLConnection)url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            readStream(in);
           
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }
    }
    
    static private void readStream(InputStream in)
    {
        BufferedReader reader = null;
        try {
          reader = new BufferedReader(new InputStreamReader(in));
          String line = "";
          while ((line = reader.readLine()) != null) {
            System.out.println(line);
            Log.d(LOG_TAG, line);
          }
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (reader != null) {
            try {
              reader.close();
            } catch (IOException e) {
              e.printStackTrace();
              }
          }
        }
      } 
}
