package com.example.bookshelf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.LayoutInflater;

import com.fedorvlasov.lazylist.ImageLoader;

public class AllBooksListAdapter extends LoanBooksListAdapter {
    private static final String LENT = "lent";

    public AllBooksListAdapter(JSONArray d, LayoutInflater inflater,
            ImageLoader imageLoader) {
        super(d, inflater, imageLoader);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isEnabled(int position) {
        boolean result = true;
        if (true) {
            JSONObject jsonObject = (JSONObject) getItem(position);
            try {
                String lent = jsonObject.getString(LENT);
                result = lent.isEmpty();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }
}