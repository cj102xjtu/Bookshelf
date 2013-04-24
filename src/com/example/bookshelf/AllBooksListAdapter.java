package com.example.bookshelf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = super.getView(position, convertView, parent);
        
        // change the background color if book is lent out.
        if (isEnabled(position)) {
            vi.setBackgroundResource(android.R.color.background_light);
        } else {
            vi.setBackgroundResource(android.R.color.darker_gray);
        }

        return vi;
    }
}