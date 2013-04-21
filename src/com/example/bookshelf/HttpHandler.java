package com.example.bookshelf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpHandler {

    private static String ACCEPT_HEADER = "Accept";
    private static String ACCEPT_HEADER_VALUE = "application/json";
    private static String CONTENT_TYPE_HEADER = "Content-Type";
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
        HttpURLConnection urlConnection = null;
        String action = null;
        if (loanBook) {
            action = URL_LOAN_BOOK;
        } else {
            action = URL_RETURN_BOOK;
        }
        try {
            // send post request
            URL url = new URL(action);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection
                    .setRequestProperty(ACCEPT_HEADER, ACCEPT_HEADER_VALUE);
            urlConnection.setRequestProperty(CONTENT_TYPE_HEADER,
                    CONTENT_TYPE_HEADER_VALUE);

            OutputStream out = new BufferedOutputStream(
                    urlConnection.getOutputStream());

            writeStream(out, bookId, userId);

            // read result
            result = readPostResult(urlConnection);

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

    static private JSONArray readStream(InputStream in) {
        BufferedReader reader = null;
        JSONArray jsonArray = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            int j = 0;
            while ((line = reader.readLine()) != null) {
                Log.d(LOG_TAG, line);
                Log.d(LOG_TAG, String.valueOf(j++));
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

    static private boolean writeStream(OutputStream out, String bookId,
            String userId) {
        boolean result = false;
        JSONObject jsonParam = new JSONObject();
        try {
            // Create JSONObject here
            jsonParam.put(USER_ID, userId);
            jsonParam.put(BOOK_ID, bookId);

            // Send POST output.
            ObjectOutputStream printout = new ObjectOutputStream(out);
//            DataOutputStream printout = new DataOutputStream(out);
//            printout.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            printout.writeObject(jsonParam.toString());
//            printout.writeObject("{\"id\": \"2\", \"lent\": \"samme\"}");
            printout.flush();
            printout.close();
            result = true;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    static private boolean readPostResult(HttpURLConnection urlConnection) {
        Log.d(LOG_TAG, "readPostResult");
        boolean result = false;
        try {
            int httpResult = urlConnection.getResponseCode();
            StringBuilder sb = new StringBuilder();
            if (httpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                Log.d(LOG_TAG,"http_OK" + sb.toString());
                result = true;

            } else {
                Log.d(LOG_TAG, urlConnection.getResponseMessage());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;

    }
}
