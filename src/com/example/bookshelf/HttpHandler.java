package com.example.bookshelf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class HttpHandler {

    private static String ACCEPT_HEADER = "Accept";
    private static String ACCEPT_HEADER_VALUE = "application/json";
    private static String CONTENT_TYPE_HEADER_VALUE = "application/json";
    private static String BOOK_ID = "id";
    private static String USER_ID = "lent";

    private static String URL_GET_ALL_BOOK = "http://108.61.84.203/~bookshel/books.php";
    private static String URL_GET_USERS_BOOK = "http://108.61.84.203/~bookshel/books.php?user=";
    private static String URL_LOAN_BOOK = "http://108.61.84.203/~bookshel/loanbook.php";
    private static String URL_RETURN_BOOK = "http://108.61.84.203/~bookshel/returnbook.php";

    private static String LOG_TAG = "HttpHandler";

    static public JSONArray getAllBooks() {
        HttpURLConnection urlConnection = null;
        JSONArray result = null;
        try {
            URL url = new URL(URL_GET_ALL_BOOK);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream());
            result = readStream(in);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return result;
    }

    static public JSONArray getUsersBook(String userId) {
        HttpURLConnection urlConnection = null;
        JSONArray result = null;

        try {
            URL url = new URL(URL_GET_USERS_BOOK + userId);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream());
            result = readStream(in);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return result;
    }

    static public boolean loanOrReturnBook(String bookId, String userId,
            boolean loanBook) {
        boolean result = false;

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
                                                                              // Limit
        HttpResponse response;
        JSONObject json = new JSONObject();
        
        // set http action
        String action = "";
        if (loanBook) {
            action = URL_LOAN_BOOK;
        } else {
            action = URL_RETURN_BOOK;
        }

        try {
            HttpPost post = new HttpPost(action);
            post.setHeader(ACCEPT_HEADER, ACCEPT_HEADER_VALUE);
            json.put(USER_ID, userId);
            json.put(BOOK_ID, bookId);
            StringEntity se = new StringEntity(json.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                    CONTENT_TYPE_HEADER_VALUE));
            post.setEntity(se);
            response = client.execute(post);

            /* Checking response */
            result = readPostResult(response);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", "Cannot Estabilish Connection");
        }

        return result;
    }

    static private JSONArray readStream(InputStream in) {
        BufferedReader reader = null;
        JSONArray jsonArray = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                Log.d(LOG_TAG, line);
                try {
                    jsonArray = new JSONArray(line);
                    Log.i(LOG_TAG, "Number of entries " + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.i(LOG_TAG, jsonObject.getString("name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        return jsonArray;
    }

    static private boolean readPostResult(HttpResponse response) {
        Log.d(LOG_TAG, "readPostResult");
        boolean result = false;
        try {
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(in));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        Log.d(LOG_TAG, line);
                    }
                    result = true;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;

    }
}
